package com.dobby.coupon.customer.loadbalance;

import com.dobby.coupon.customer.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author Dooby Kim
 * @Date 2023/6/16 4:22 下午
 * @Version 1.0
 * @Desc 自定义的负载均衡规则类
 */
@Slf4j
public class CanaryRule implements ReactorServiceInstanceLoadBalancer {

    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private String serviceId;

    // 定义一个轮询策略的种子
    final AtomicInteger position;

    public CanaryRule(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                      String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
        position = new AtomicInteger(new Random().nextInt(1000));
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances, request));
    }

    private Response<ServiceInstance> processInstanceResponse(
            ServiceInstanceListSupplier supplier,
            List<ServiceInstance> serviceInstances,
            Request request) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, request);

        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    /**
     * 金丝雀测试负载均衡策略
     *
     * @param instances
     * @param request
     * @return
     */
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        // 注册中心无可用实例 抛出异常
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("No instance available {}", serviceId);
            return new EmptyResponse();
        }

        // 从请求Header中获取特定的流量打标值
        // 注意：以下代码仅适用于WebClient调用，如果使用RestTemplate或者Feign则需要额外适配
        DefaultRequestContext context = (DefaultRequestContext) request.getContext();
        RequestData requestData = (RequestData) context.getClientRequest();
        HttpHeaders headers = requestData.getHeaders();

        String trafficVersion = headers.getFirst(Constant.TRAFFIC_VERSION);

        // 如果没有找到打标标记，或者标记为空，则使用RoundRobin规则进行轮训
        if (StringUtils.isBlank(trafficVersion)) {
            // 过滤掉所有金丝雀测试的节点（ Metadata 有值的节点）
            List<ServiceInstance> noneCanaryInstances = instances.stream()
                    .filter(e -> !e.getMetadata().containsKey(Constant.TRAFFIC_VERSION))
                    .collect(Collectors.toList());
            return getRoundRobinInstance(noneCanaryInstances);
        }

        // 找出所有金丝雀服务器，用RoundRobin算法挑出一台
        List<ServiceInstance> canaryInstances = instances.stream().filter(e -> {
            String trafficVersionInMetadata = e.getMetadata().get(Constant.TRAFFIC_VERSION);
            return StringUtils.equalsIgnoreCase(trafficVersionInMetadata, trafficVersion);
        }).collect(Collectors.toList());

        return getRoundRobinInstance(canaryInstances);
    }

    /**
     * 同集群优先负载均衡策略，即优先调用同一个 Cluster 的服务器，如果同一个 Cluster 中没有可用的服务，再调用其他 Cluster 的服务
     *
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getSameClusterService(List<ServiceInstance> instances) {
        // 注册中心无可用实例 抛出异常
        if (CollectionUtils.isEmpty(instances)) {
            log.warn("No instance available {}", serviceId);
            return new EmptyResponse();
        }
        Environment environment = new StandardEnvironment();
        final String clusterName = environment.resolvePlaceholders("${spring.cloud.nacos.discovery.cluster-name:}");
        final List<ServiceInstance> collect = instances.stream().filter(e -> {
            final Map<String, String> metadata = e.getMetadata();
            final String serviceClusterName = metadata.get("nacos.cluster");
            return serviceClusterName.equals(clusterName);
        }).collect(Collectors.toList());

        // 有同集群下服务 RoundRobin 算法挑选
        if (CollectionUtils.isEmpty(collect)) {
            return getRoundRobinInstance(instances);
        } else {
            return getRoundRobinInstance(collect);
        }
    }

    /**
     * 使用 RoundRobin 轮训机制获取节点
     *
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getRoundRobinInstance(List<ServiceInstance> instances) {
        // 如果没有可用节点，则返回空
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + serviceId);
            return new EmptyResponse();
        }

        int pos = Math.abs(this.position.incrementAndGet());
        ServiceInstance instance = instances.get(pos % instances.size());

        return new DefaultResponse(instance);
    }

}
