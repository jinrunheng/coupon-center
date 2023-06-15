package com.dobby.coupon.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @Author Dooby Kim
 * @Date 2023/5/22 10:38 下午
 * @Version 1.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
public class CouponTemplateServApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponTemplateServApplication.class, args);
    }
}
