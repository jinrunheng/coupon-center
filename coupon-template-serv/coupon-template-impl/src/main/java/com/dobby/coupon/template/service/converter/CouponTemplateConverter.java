package com.dobby.coupon.template.service.converter;

import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dobby.coupon.template.dao.entity.CouponTemplate;

/**
 * @Author Dooby Kim
 * @Date 2023/5/22 9:52 下午
 * @Version 1.0
 */
public class CouponTemplateConverter {
    public static CouponTemplateInfo convertToTemplateInfo(CouponTemplate template) {
        return CouponTemplateInfo.builder()
                .id(template.getId())
                .name(template.getName())
                .desc(template.getDescription())
                .type(template.getCategory().getCode())
                .shopId(template.getShopId())
                .available(template.getAvailable())
                .rule(template.getRule())
                .build();
    }
}
