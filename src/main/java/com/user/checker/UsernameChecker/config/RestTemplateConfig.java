package com.user.checker.UsernameChecker.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {


    @Bean
    public RestTemplate restTemplate()
    {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(900);
        connectionManager.setDefaultMaxPerRoute(300);

        CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(closeableHttpClient));
    }
}
