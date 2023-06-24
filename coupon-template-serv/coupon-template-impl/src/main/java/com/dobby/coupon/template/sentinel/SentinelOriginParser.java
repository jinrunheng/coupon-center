package com.dobby.coupon.template.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author Dooby Kim
 * @Date 2023/6/24 3:09 下午
 * @Version 1.0
 * @Desc 实现对针对调用源的限流；在上游的 coupon-customer-serv 服务中，我们添加了请求头 SentinelSource = coupon-customer-serv，Sentinel 只会对包含 SentinelSource 请求头的流量限流
 */
@Component
@Slf4j
public class SentinelOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        log.info("request {}, header={}", httpServletRequest.getParameterMap(), httpServletRequest.getHeaderNames());
        return httpServletRequest.getHeader("SentinelSource");
    }
}
