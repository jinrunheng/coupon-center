package com.dobby.coupon.customer.service;

import com.dobby.coupon.customer.dao.entity.Coupon;
import com.dobby.coupon.template.api.beans.CouponInfo;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 9:53 下午
 * @Version 1.0
 */
public class CouponConverter {
    public static CouponInfo convertToCoupon(Coupon coupon) {

        return CouponInfo.builder()
                .id(coupon.getId())
                .status(coupon.getStatus().getCode())
                .templateId(coupon.getTemplateId())
                .shopId(coupon.getShopId())
                .userId(coupon.getUserId())
                .template(coupon.getTemplateInfo())
                .build();
    }
}
