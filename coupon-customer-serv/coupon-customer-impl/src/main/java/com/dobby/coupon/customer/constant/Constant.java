package com.dobby.coupon.customer.constant;

/**
 * @Author Dooby Kim
 * @Date 2023/6/16 3:36 下午
 * @Version 1.0
 * @Desc 定义常量类
 */
public interface Constant {
    /**
     * 特定的流量 Key，如果 WebClient 发送一个请求，其 Header的 key-value 列表中包含这个 Key，那么这个请求就被识别为一个"金丝雀"测试请求
     */
    String TRAFFIC_VERSION = "traffic-version";
}
