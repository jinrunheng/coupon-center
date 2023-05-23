package com.dobby.coupon.calculation.api.beans;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author Dooby Kim
 * @Date 2023/5/23 7:00 下午
 * @Version 1.0
 * @Desc 订单试算结果
 */
@Data
@NoArgsConstructor
public class SimulationResponse {

    /**
     * 最省钱的优惠券 ID
     */
    private Long bestCouponId;

    /**
     * 每一个 coupon ID 对应的订单价格
     */
    private Map<Long, Long> couponToOrderPrice = Maps.newHashMap();
}
