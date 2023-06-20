package com.dobby.coupon.customer.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dooby Kim
 * @Date 2023/6/20 10:18 上午
 * @Version 1.0
 * @Desc 指定 OpenFeign 的日志级别为 FULL，OpenFeign 共有四种日志级别：NONE，BASIC，HEADERS，FULL
 */
@Configuration
public class FeignLoggingLevelConfiguration {

    @Bean
    Logger.Level feignLogger() {
        return Logger.Level.FULL;
    }
}
