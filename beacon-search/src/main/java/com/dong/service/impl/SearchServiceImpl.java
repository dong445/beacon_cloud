package com.dong.service.impl;

import com.dong.common.constant.RabbitMQConstants;
import com.dong.common.enums.ExceptionEnums;
import com.dong.common.exception.SearchException;
import com.dong.common.model.StandardReport;
import com.dong.service.SearchService;
import com.dong.utils.SearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    //添加成功的result
    private final String CREATED = "created";

    // 修改成功的result
    private final String UPDATED = "updated";

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void index(String index, String id, String json) throws IOException {
        //构建插入数据的Request
        IndexRequest request = new IndexRequest();

        //给request对象封装索引信息，文档id，以及文档内容
        request.index(index);
        request.id(id);
        request.source(json, XContentType.JSON);

        //将request信息发送给ES服务
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        //校验添加是否成功
        String result = response.getResult().getLowercase();
        // created = 该 id 首次写入成功；updated = 该 id 已存在、本次为覆盖写成功（同一个 id 被重复写入时返回）
        if(!CREATED.equals(result) /*&& !UPDATED.equals(result)*/){
            log.error("【搜索模块-写入数据失败】 index = {},id = {},json = {},result = {}",index,id,json,result);
            throw new SearchException(ExceptionEnums.SEARCH_INDEX_ERROR);
        }
        log.info("【搜索模块-写入数据成功】 索引添加成功index = {},id = {},json = {},result = {}",index,id,json,result);
    }

    @Override
    public boolean exists(String index, String id) throws IOException {
        //构建GetRequest，查看索引是否存在
        GetRequest request=new GetRequest();

        //指定索引信息，还有文档id
        request.index(index);
        request.id(id);

        //基于restHighLevelClient将查询指定id文档是否存在的请求投递过去
        boolean exists=restHighLevelClient.exists(request,RequestOptions.DEFAULT);

        //直接返回信息
        return exists;
    }

    @Override
    public void update(String index, String id, Map<String, Object> doc) throws IOException {
        //基于exists方法，查询当前文档是否存在
        boolean exists = exists(index, id);
        if (!exists) {
            // 当前文档不存在
            StandardReport report = SearchUtils.get();
            if (report.getReUpdate()) {
                // 第二次获取投递的消息，到这已经是延迟20s了。
                log.error("【搜索模块-修改日志】 修改日志失败，report = {}", report);
            } else {
                // 第一次投递，可以再次将消息仍会MQ中
                // 开始第二次消息的投递了
                report.setReUpdate(true);
                rabbitTemplate.convertAndSend(RabbitMQConstants.SMS_GATEWAY_NORMAL_QUEUE, report);
            }
            //记的清除一下
            SearchUtils.remove();
            return;
        }
        //到这，可以确认文档是存在的，直接做修改操作
        UpdateRequest request = new UpdateRequest();

        request.index(index);
        request.id(id);
        request.doc(doc);

        UpdateResponse update = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        String result = update.getResult().getLowercase();
        if (!UPDATED.equals(result)) {
            // 添加失败！！
            log.error("【搜索模块-修改日志失败】 index = {},id = {},doc = {}", index, id, doc);
            throw new SearchException(ExceptionEnums.SEARCH_UPDATE_ERROR);
        }
        log.info("【搜索模块-修改日志成功】 文档修改成功index = {},id = {},doc = {}", index, id, doc);
    }

    @Override
    public Map<String, Object> findSmsByParameters(Map<String, Object> parameters) throws IOException {
        // 1. 声明请求（默认查当前年份索引）
        SearchRequest request = new SearchRequest(SearchUtils.getCurrYearIndex());

        // 2. 取参数
        int from = Integer.parseInt(String.valueOf(parameters.getOrDefault("from", 0)));
        int size = Integer.parseInt(String.valueOf(parameters.getOrDefault("size", 10)));
        Object contentObj = parameters.get("content");
        Object mobileObj = parameters.get("mobile");
        Object startTimeObj = parameters.get("starttime");
        Object stopTimeObj = parameters.get("stoptime");
        Object clientIDObj = parameters.get("clientID");
        // 3. clientID 可能是 List（普通用户传的全部），也可能是单个字符串
        List<Long> clientIDList = null;
        if (clientIDObj instanceof List) {
            clientIDList = (List<Long>) clientIDObj;
        } else if (clientIDObj != null && !"".equals(clientIDObj)) {
            clientIDList = Collections.singletonList(Long.parseLong(String.valueOf(clientIDObj)));
        }

        // 4. 拼条件
        SearchSourceBuilder source = new SearchSourceBuilder();
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        // 4.1 关键字（全文检索 + 高亮）
        if (contentObj != null && !"".equals(contentObj)) {
            bool.must(QueryBuilders.matchQuery("text", contentObj));
            HighlightBuilder hl = new HighlightBuilder();
            hl.field("text").preTags("<span style='color:red'>").postTags("</span>");
            source.highlighter(hl);
        }
        // 4.2 手机号（前缀匹配）
        if (mobileObj != null && !"".equals(mobileObj)) {
            bool.must(QueryBuilders.prefixQuery("mobile", String.valueOf(mobileObj)));
        }
        // 4.3 开始时间（前端 Date.parse() 传毫秒时间戳，经 Feign 后是字符串，转成 Long）
        Long startTime = toLong(startTimeObj);
        if (startTime != null) {
            bool.must(QueryBuilders.rangeQuery("sendTime").gte(startTime));
        }
        // 4.4 结束时间
        Long stopTime = toLong(stopTimeObj);
        if (stopTime != null) {
            bool.must(QueryBuilders.rangeQuery("sendTime").lte(stopTime));
        }
        // 4.5 客户 id
        if (clientIDList != null) {
            bool.must(QueryBuilders.termsQuery("clientId", clientIDList));
        }

        source.from(from).size(size).query(bool);
        request.source(source);

        // 5. 执行查询
        SearchResponse resp = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 6. 封装数据（字段名对齐前端 search.js 的 columns）
        long total = resp.getHits().getTotalHits().value;
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SearchHit hit : resp.getHits().getHits()) {
            Map<String, Object> row = hit.getSourceAsMap();
            // 公司名 = ES 的 sign
            row.put("corpname", row.get("sign"));
            // 发送时间：可能是字符串（新数据）、毫秒时间戳、或数组（旧数据），统一转成字符串
            Object sendTimeObj = row.get("sendTime");
            if (sendTimeObj != null) {
                row.put("sendTimeStr", formatSendTime(sendTimeObj));
            }
            // 高亮结果在 highlight 里，不在 _source 里，要单独回填
            Map<String, HighlightField> hf = hit.getHighlightFields();
            if (hf.get("text") != null && hf.get("text").getFragments().length > 0) {
                row.put("text", hf.get("text").getFragments()[0].string());
            }
            rows.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("rows", rows);
        return result;
    }

    /** 参数转 Long；null 或空串返回 null（不能用 Nacos 的 ConvertUtils.toLong，它遇空串会抛异常） */
    private Long toLong(Object obj) {
        if (obj == null) {
            return null;
        }
        String s = String.valueOf(obj).trim();
        if (s.isEmpty()) {
            return null;
        }
        return Long.parseLong(s);
    }

    /** 把 _source 里的 sendTime 统一转成显示字符串 */
    private String formatSendTime(Object sendTimeObj) {
        if (sendTimeObj instanceof Number) {
            // 存的是 epoch millis 毫秒时间戳
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(((Number) sendTimeObj).longValue()));
        }
        if (sendTimeObj instanceof List) {
            // 旧数据：曾经被序列化成数组 [年,月,日,时,分,秒]
            return listToDateString((List<?>) sendTimeObj);
        }
        // 新数据：已经是 "yyyy-MM-dd HH:mm:ss" 字符串
        return String.valueOf(sendTimeObj);
    }

    /** 旧数据：把 [年,月,日,时,分,秒] 数组拼成字符串 */
    private String listToDateString(List<?> sendTime) {
        int year = ((Number) sendTime.get(0)).intValue();
        int month = ((Number) sendTime.get(1)).intValue();
        int day = ((Number) sendTime.get(2)).intValue();
        int hour = ((Number) sendTime.get(3)).intValue();
        int minute = ((Number) sendTime.get(4)).intValue();
        int second = sendTime.size() > 5 ? ((Number) sendTime.get(5)).intValue() : 0;
        return year + "-" + pad(month) + "-" + pad(day) + " " + pad(hour) + ":" + pad(minute) + ":" + pad(second);
    }

    private String pad(int v) {
        return v < 10 ? "0" + v : "" + v;
    }
}
