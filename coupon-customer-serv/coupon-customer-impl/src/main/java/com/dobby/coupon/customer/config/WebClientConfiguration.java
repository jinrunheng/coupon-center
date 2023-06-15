package com.dobby.coupon.customer.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @Author Dooby Kim
 * @Date 2023/6/15 4:12 下午
 * @Version 1.0
 * @Desc 构建 WebClient 的配置类
 */
@Configuration
public class WebClientConfiguration {

    /**
     * 注册 Bean 并添加负载均衡功能
     *
     * @return
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder register() {
        return WebClient.builder();
    }
}
