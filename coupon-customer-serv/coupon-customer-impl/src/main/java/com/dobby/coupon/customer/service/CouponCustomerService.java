package com.dobby.coupon.customer.service;

import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.api.beans.SimulationOrder;
import com.dobby.coupon.calculation.api.beans.SimulationResponse;
import com.dobby.coupon.customer.dao.entity.Coupon;
import com.dobby.coupon.template.api.beans.CouponInfo;
import com.dooby.coupon.customer.api.beans.RequestCoupon;
import com.dooby.coupon.customer.api.beans.SearchCoupon;

import java.util.List;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 9:47 下午
 * @Version 1.0
 * @Desc 用户对接服务
 */
public interface CouponCustomerService {

    /**
     * 领券接口
     *
     * @param request
     * @return
     */
    Coupon requestCoupon(RequestCoupon request);

    /**
     * 核销优惠券
     *
     * @param info
     * @return
     */
    ShoppingCart placeOrder(ShoppingCart info);

    /**
     * 优惠券金额试算
     *
     * @param order
     * @return
     */
    SimulationResponse simulateOrderPrice(SimulationOrder order);

    /**
     * 用户删除优惠券
     *
     * @param userId
     * @param couponId
     */
    void deleteCoupon(Long userId, Long couponId);

    /**
     * 查询用户优惠券
     *
     * @param request
     * @return
     */
    List<CouponInfo> findCoupon(SearchCoupon request);
}
