package com.dooby.coupon.customer.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 6:30 下午
 * @Version 1.0
 * @Desc 封装优惠券查询的请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCoupon {

    @NotNull
    private Long userId;
    private Long shopId;
    private Integer couponStatus;
}
