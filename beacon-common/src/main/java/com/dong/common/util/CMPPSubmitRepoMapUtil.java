package com.dong.common.util;

import com.dong.common.model.StandardSubmit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信提交信息仓库。
 * <p>
 * 以 CMPP 消息流水号（sequenceId / msgId）为 key 缓存下发的 {@link StandardSubmit}，
 * 用于收到运营商应答（CMPP_SUBMIT_RESP）或状态报告（CMPP_DELIVER）时反查原始提交信息。
 */
public class CMPPSubmitRepoMapUtil {
    /*
    *  - static：类加载时创建一次，整个 JVM 进程里只有一份，所有调用方共享同一个 Map（不是每调一次新建一个）。
      - ConcurrentHashMap：并发安全。多个线程（Netty 的 IO 线程、MQ 消费线程）同时 put/get 不会互相干扰，内部用分段/CAS
        而不是整个锁住。
      - 数据就存在 JVM 堆内存里，也就是常说的「内存存储」——不写数据库、不写文件、不进 Redis
      * 这个存储「永不自动过期」，只会在 进程重启、sequence 复用覆盖、内存耗尽失效
    * */
    private static ConcurrentHashMap<String, StandardSubmit> map = new ConcurrentHashMap<>();


    public static void put(int sequence,StandardSubmit submit){
        map.put(sequence + "",submit);
    }

    public static StandardSubmit get(int sequence){
        return map.get(sequence + "");
    }

    public static StandardSubmit remove(int sequence){
        return map.remove(sequence + "");
    }

}
