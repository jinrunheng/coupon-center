package com.dooby.coupon.customer.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 6:22 下午
 * @Version 1.0
 * @Desc 优惠券状态
 */
@Getter
@AllArgsConstructor
public enum CouponStatus {
    AVAILABLE("未使用", 1),
    USED("已用", 2),
    INACTIVE("已经注销", 3);

    private String desc;
    private Integer code;

    public static CouponStatus convert(Integer code) {
        if (code == null) {
            return null;
        }
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElse(null);
    }
}
