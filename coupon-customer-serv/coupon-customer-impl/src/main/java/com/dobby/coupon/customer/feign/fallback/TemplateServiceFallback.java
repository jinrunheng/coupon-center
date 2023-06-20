package com.dobby.coupon.customer.feign.fallback;

import com.dobby.coupon.customer.feign.TemplateService;
import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * @Author Dooby Kim
 * @Date 2023/6/20 10:31 上午
 * @Version 1.0
 * @Desc 定义 fallback 类方式，实现 OpenFeign 降级
 */
@Slf4j
@Component
public class TemplateServiceFallback implements TemplateService {
    @Override
    public CouponTemplateInfo getTemplate(Long id) {
        log.info("fallback getTemplate");
        return null;
    }

    @Override
    public Map<Long, CouponTemplateInfo> getTemplateInBatch(Collection<Long> ids) {
        log.info("fallback getTemplateInBatch");
        return null;
    }
}
