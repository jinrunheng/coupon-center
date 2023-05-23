package com.dobby.coupon.calculation.template;

import com.dobby.coupon.calculation.api.beans.ShoppingCart;

/**
 * @Author Dooby Kim
 * @Date 2023/5/23 7:17 下午
 * @Version 1.0
 */
public interface RuleTemplate {

    /**
     * 计算优惠券
     *
     * @param settlement
     * @return
     */
    ShoppingCart calculate(ShoppingCart settlement);
}
