package com.dobby.gateway.dynamic;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @Author Dooby Kim
 * @Date 2023/8/2 12:47 上午
 * @Version 1.0
 * @Desc 自定义监听器获取 Nacos Config 的配置变化
 */
@Slf4j
@Component
public class DynamicRoutesListener implements Listener {

    @Autowired
    private GatewayService gatewayService;

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        log.info("routes changes: {}", configInfo);
        final List<RouteDefinition> routeDefinitions = JSON.parseArray(configInfo, RouteDefinition.class);
        gatewayService.updateRoutes(routeDefinitions);
    }
}
