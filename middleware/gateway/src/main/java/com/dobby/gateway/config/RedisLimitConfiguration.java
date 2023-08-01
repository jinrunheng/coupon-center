package com.dobby.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * @Author Dooby Kim
 * @Date 2023/7/14 4:47 下午
 * @Version 1.0
 * @Desc 定义 Redis + Lua 做网关层限流参数的配置类
 */
@Configuration
public class RedisLimitConfiguration {

    // 限流的纬度
    // 基于令牌桶算法
    @Bean
    @Primary
    public KeyResolver remoteHostLimitationKey() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }


    // template 服务限流规则
    @Bean("templateRateLimiter")
    public RedisRateLimiter templateRateLimiter() {
        // 第一个参数表示的每秒发放令牌的数量，第二个参数表示令牌桶的的容量
        return new RedisRateLimiter(10, 20);
    }

    // customer 服务限流规则
    @Bean("customerRateLimiter")
    public RedisRateLimiter customerRateLimiter() {
        return new RedisRateLimiter(20, 40);
    }

    @Bean
    @Primary
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(50, 100);
    }
}
