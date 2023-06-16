package com.dooby.coupon.customer.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 6:27 下午
 * @Version 1.0
 * @Desc 封装用户领取优惠券的请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCoupon {

    /**
     * 用户 ID
     */
    @NotNull
    private Long userId;

    /**
     * 券模版 ID
     */
    @NotNull
    private Long couponTemplateId;

    /**
     * 用于 Loadbalancer 测试流量打标
     */
    private String trafficVersion;
}
