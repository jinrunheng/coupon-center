package com.dobby.coupon.calculation.api.beans;

import com.dobby.coupon.template.api.beans.CouponInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author Dooby Kim
 * @Date 2023/5/23 6:51 下午
 * @Version 1.0
 * @Desc 购物车，用于封装订单信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    /**
     * 订单商品列表
     */
    @NotEmpty
    private List<Product> products;

    // 封装了优惠券信息，目前计算服务只支持单张优惠券
    // 为了考虑到以后多券的扩展性，所以定义成了List
    private Long couponId;
    private List<CouponInfo> couponInfos;

    /**
     * 订单的最终价格
     */
    private Long cost;

    /**
     * 用户 ID
     */
    @NotNull
    private Long userId;
}
