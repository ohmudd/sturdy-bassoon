package cn.easyes.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;

import java.io.IOException;

/**
 * elasticsearch 版本工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EsVersionUtil {

    /**
     * 支持的版本 目前支持版本为7.xx
     */
    private final static String supportedVersion = "7";

    /**
     * 获取es jar包版本
     *
     * @param restHighLevelClient es 高级客户端
     * @return jar version
     */
    public static String getJarVersion(RestHighLevelClient restHighLevelClient) {
        String version = restHighLevelClient.getClass().getPackage().getImplementationVersion();
        LogUtils.formatInfo("elasticsearch jar version:%s", version);
        return version;
    }

    /**
     * 获取elasticsearch client 版本
     *
     * @param restHighLevelClient es高级客户端
     * @return client version
     */
    public static String getClientVersion(RestHighLevelClient restHighLevelClient) {
        MainResponse info;
        try {
            info = restHighLevelClient.info(RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String version = info.getVersion().getNumber();
        LogUtils.formatInfo("elasticsearch client version:%s", version);
        return version;
    }

    /**
     * 校验es client版本及jar包版本
     *
     * @param restHighLevelClient es高级客户端
     */
    public static void verify(RestHighLevelClient restHighLevelClient) {
        String jarVersion = getJarVersion(restHighLevelClient);
        if (!jarVersion.startsWith(supportedVersion)) {
            throw ExceptionUtils.eee("Easy-Es supported elasticsearch jar version is:%s.xx", supportedVersion);
        }
        String clientVersion = getClientVersion(restHighLevelClient);
        if (!clientVersion.startsWith(supportedVersion)) {
            throw ExceptionUtils.eee("Easy-Es supported elasticsearch client version is:%s.xx", supportedVersion);
        }
        if (!jarVersion.equals(clientVersion)) {
            LogUtils.formatError("elasticsearch clientVersion:%s not equals jarVersion:%s", clientVersion, jarVersion);
        }
    }
}
