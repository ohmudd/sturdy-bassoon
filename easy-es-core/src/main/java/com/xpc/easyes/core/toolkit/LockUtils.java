package com.xpc.easyes.core.toolkit;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import static com.xpc.easyes.core.constants.BaseEsConstants.*;

/**
 * 基于es写的轻量级分布式锁,仅供框架内部使用,可避免引入redis/zk等其它依赖
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
public class LockUtils {
    /**
     * 锁所在索引
     */
    private final static String LOCK_INDEX = "ee-distribute-lock";
    /**
     * id字段名
     */
    private final static String ID_FIELD = "_id";
    /**
     * 重试等待时间
     */
    private final static Integer WAIT_SECONDS = 60;

    public static synchronized boolean tryLock(RestHighLevelClient client, String idValue, Integer maxRetry) {
        boolean existsIndex = IndexUtils.existsIndex(client, LOCK_INDEX);
        if (!existsIndex) {
            IndexUtils.createEmptyIndex(client, LOCK_INDEX);
        }

        if (maxRetry <= ZERO) {
            return Boolean.FALSE;
        }

        if (getCount(client, idValue) > ZERO) {
            try {
                Thread.sleep(WAIT_SECONDS / maxRetry);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return tryLock(client, idValue, --maxRetry);
        } else {
            return createLock(client, idValue);
        }
    }

    private static boolean createLock(RestHighLevelClient client, String idValue) {
        IndexRequest indexRequest = new IndexRequest(LOCK_INDEX);
        indexRequest.id(idValue);
        indexRequest.source(DISTRIBUTED_LOCK_TIP_JSON, XContentType.JSON);
        IndexResponse response;
        try {
            response = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        return response.status().equals(RestStatus.CREATED);
    }

    public synchronized static boolean release(RestHighLevelClient client, String idValue, Integer maxRetry) {
        DeleteRequest deleteRequest = new DeleteRequest(LOCK_INDEX);
        deleteRequest.id(idValue);
        if (maxRetry <= ZERO) {
            return Boolean.FALSE;
        }

        DeleteResponse response;
        try {
            response = client.delete(deleteRequest, RequestOptions.DEFAULT);
            System.out.println(response.status());
        } catch (IOException e) {
            return retryRelease(client, idValue, --maxRetry);
        }
        if (RestStatus.OK.equals(response.status())) {
            return Boolean.TRUE;
        } else {
            return retryRelease(client, idValue, maxRetry);
        }
    }

    private static boolean retryRelease(RestHighLevelClient client, String idValue, Integer maxRetry) {
        try {
            Thread.sleep(WAIT_SECONDS / maxRetry);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        return release(client, idValue, --maxRetry);
    }

    private static Integer getCount(RestHighLevelClient client, String idValue) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(LOCK_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery(ID_FIELD, idValue));
        searchRequest.source(searchSourceBuilder);
        SearchResponse response;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return ONE;
        }
        return (int) response.getHits().getTotalHits().value;
    }

}
