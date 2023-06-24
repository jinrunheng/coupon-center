package com.dobby.coupon.customer.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dooby Kim
 * @Date 2023/6/24 3:04 下午
 * @Version 1.0
 * @Desc 实现针对调用源的请求头设置
 */
@Configuration
public class OpenfeignSentinelInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("SentinelSource", "coupon-customer-serv");
    }
}
