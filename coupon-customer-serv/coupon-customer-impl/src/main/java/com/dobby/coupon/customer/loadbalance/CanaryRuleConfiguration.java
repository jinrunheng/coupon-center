package com.dobby.coupon.customer.loadbalance;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @Author Dooby Kim
 * @Date 2023/6/16 4:34 下午
 * @Version 1.0
 */
public class CanaryRuleConfiguration {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new CanaryRule(loadBalancerClientFactory.getLazyProvider(name,
                ServiceInstanceListSupplier.class), name);
    }

    //    @Bean
//    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
//                                                                   LoadBalancerClientFactory loadBalancerClientFactory){
//        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
//        return new RoundRobinLoadBalancer(loadBalancerClientFactory
//                .getLazyProvider(name, ServiceInstanceListSupplier.class),
//                name);
//    }
}
