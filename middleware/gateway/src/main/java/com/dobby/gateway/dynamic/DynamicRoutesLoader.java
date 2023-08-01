package com.dobby.gateway.dynamic;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Dooby Kim
 * @Date 2023/8/2 12:53 上午
 * @Version 1.0
 * @Desc DynamicRoutesLoader 实现了 InitializingBean 接口，实现该接口会在当前类所有的属性加载完成后，执行一段定义在 afterPropertiesSet 方法中的自定义逻辑
 * 在 afterPropertiesSet 方法中，我们做了两件事：
 * 1. 调用 Nacos 提供的 NacosConfigManager 类加载指定的路由配置文件，配置文件名是 routes-config.json
 * 2. 将定义的 DynamicRoutesListener 注册到 routes-config.json 文件的监听列表中，这样一来，每次这个文件发生变动，监听器都能够获取到通知
 */
@Slf4j
@Configuration
public class DynamicRoutesLoader implements InitializingBean {

    @Autowired
    private NacosConfigManager configManager;

    @Autowired
    private NacosConfigProperties configProperties;

    @Autowired
    private DynamicRoutesListener dynamicRoutesListener;

    private static final String ROUTES_CONFIG = "routes-config.json";

    @Override
    public void afterPropertiesSet() throws Exception {
        // 首次加载配置
        String routes = configManager.getConfigService()
                .getConfig(ROUTES_CONFIG, configProperties.getGroup(), 10000);

        dynamicRoutesListener.receiveConfigInfo(routes);

        // 注册监听器
        configManager.getConfigService()
                .addListener(ROUTES_CONFIG, configProperties.getGroup(), dynamicRoutesListener);
    }
}
