package com.dobby.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * @Author Dooby Kim
 * @Date 2023/7/14 4:24 下午
 * @Version 1.0
 */
@Configuration
public class RoutesConfiguration {


    @Autowired
    private KeyResolver remoteHostLimitationKey;

    @Autowired
    @Qualifier("templateRateLimiter")
    private RateLimiter templateRateLimiter;

    @Autowired
    @Qualifier("customerRateLimiter")
    private RateLimiter customerRateLimiter;

    @Bean
    public RouteLocator declare(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(route -> route.path("/gateway/coupon-customer/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(limiter -> {
                                            limiter.setKeyResolver(remoteHostLimitationKey);
                                            limiter.setRateLimiter(customerRateLimiter);
                                            limiter.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                                        }
                                )
                        )
                        .uri("lb://coupon-customer-serv")
                ).route(route -> route
                        // 如果一个请求命中了多个路由，order越小的路由优先级越高
                        .order(1)
                        .path("/gateway/template/**")
                        .filters(f -> f.stripPrefix(1)
                                .requestRateLimiter(c -> {
                                            c.setKeyResolver(remoteHostLimitationKey);
                                            c.setRateLimiter(templateRateLimiter);
                                            c.setStatusCode(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED);
                                        }
                                )
                        )
                        .uri("lb://coupon-template-serv")
                ).route(route -> route
                        .path("/gateway/calculator/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://coupon-calculation-serv")
                ).build();
    }
}
