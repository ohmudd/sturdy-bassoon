package cn.easyes.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * elasticsearch 版本工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EsVersionUtil {

    /**
     * 获取es jar包版本
     *
     * @param restHighLevelClient es 高级客户端
     */
    public void getJarVersion(RestHighLevelClient restHighLevelClient) {
        String version = restHighLevelClient.getClass().getPackage().getImplementationVersion();
        System.out.println("jar version = " + version);
    }
}
