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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

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
        if(!CREATED.equals(result)){
            // 添加失败！！
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

}
