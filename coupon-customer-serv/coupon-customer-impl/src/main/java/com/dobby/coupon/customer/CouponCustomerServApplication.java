package com.dobby.coupon.customer;

import com.dobby.coupon.customer.loadbalance.CanaryRuleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author Dooby Kim
 * @Date 2023/5/28 4:33 下午
 * @Version 1.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.dobby"})
@EntityScan(basePackages = {"com.dobby"})
@LoadBalancerClient(value = "coupon-template-serv", configuration = CanaryRuleConfiguration.class)
@EnableFeignClients(basePackages = {"com.dobby"})
public class CouponCustomerServApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponCustomerServApplication.class, args);
    }
}
