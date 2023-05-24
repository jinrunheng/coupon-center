package com.dobby.coupon.calculation.service;

import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.api.beans.SimulationOrder;
import com.dobby.coupon.calculation.api.beans.SimulationResponse;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 7:34 下午
 * @Version 1.0
 */
public interface CouponCalculationService {

    ShoppingCart calculateOrderPrice(ShoppingCart cart);

    SimulationResponse simulateOrder(SimulationOrder cart);
}
