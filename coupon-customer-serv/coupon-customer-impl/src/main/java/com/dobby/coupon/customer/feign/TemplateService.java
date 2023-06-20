package com.dobby.coupon.customer.feign;

import com.dobby.coupon.customer.feign.fallback.TemplateServiceFallback;
import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;

/**
 * @Author Dooby Kim
 * @Date 2023/6/19 9:53 上午
 * @Version 1.0
 * @Desc OpenFeign 用于实现对 coupon-template-serv 的远程调用代理
 */
@FeignClient(value = "coupon-template-serv", path = "/template", fallback = TemplateServiceFallback.class)
public interface TemplateService {

    /**
     * 读取优惠券
     *
     * @param id
     * @return
     */
    @GetMapping("/getTemplate")
    CouponTemplateInfo getTemplate(@RequestParam("id") Long id);

    /**
     * 批量获取优惠券
     *
     * @param ids
     * @return
     */
    @GetMapping("/getBatch")
    Map<Long, CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids);
}
