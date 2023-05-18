package com.dobby.coupon.template.api.enums;

import org.junit.jupiter.api.Assertions;

/**
 * @Author Dooby Kim
 * @Date 2023/5/18 9:29 下午
 * @Version 1.0
 */
class CouponTypeTest {

    @org.junit.jupiter.api.Test
    void convert() {
        String code = "3";
        String errorCode = "100";
        Assertions.assertEquals(CouponType.convert(code), CouponType.RANDOM_DISCOUNT);
        Assertions.assertEquals(CouponType.convert(errorCode), CouponType.UNKNOWN);
    }
}