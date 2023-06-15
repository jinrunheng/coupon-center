package com.dobby.coupon.calculation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 8:03 下午
 * @Version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CouponCalculationServApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponCalculationServApplication.class, args);
    }
}
