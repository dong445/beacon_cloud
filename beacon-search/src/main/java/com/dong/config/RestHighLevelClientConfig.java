package com.dong.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
@RefreshScope
public class RestHighLevelClientConfig {

    @Value("${elasticsearch.hostAndPorts}")
    private List<String> hostAndPorts;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 初始化连接 ES 的 HttpHost 信息
        HttpHost[] httpHosts = new HttpHost[hostAndPorts.size()];
        for (int i = 0; i < hostAndPorts.size(); i++) {
            String[] hostAndPort = hostAndPorts.get(i).split(":");
            httpHosts[i] = new HttpHost(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
        }

        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);

        // 有用户名密码时，添加 Basic Auth 认证
        if (StringUtils.hasText(username)) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password)
            );

            restClientBuilder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
            );
        }

        // 构建连接 ES 的 client 对象
        return new RestHighLevelClient(restClientBuilder);
    }
}
