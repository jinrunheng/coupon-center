package com.dobby.coupon.customer.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.api.beans.SimulationOrder;
import com.dobby.coupon.calculation.api.beans.SimulationResponse;
import com.dobby.coupon.customer.dao.entity.Coupon;
import com.dobby.coupon.customer.service.CouponCustomerService;
import com.dobby.coupon.template.api.beans.CouponInfo;
import com.dooby.coupon.customer.api.beans.RequestCoupon;
import com.dooby.coupon.customer.api.beans.SearchCoupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author Dooby Kim
 * @Date 2023/5/28 4:27 下午
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("coupon-customer")
@RefreshScope
public class CouponCustomerController {

    /**
     * disableCouponRequest 属性为开启动态配置推送的开关，其控制着是否发放优惠券，如果值为 true 则暂停领券；我们给定了初始默认值为 false
     */
    @Value("${disableCouponRequest:false}")
    private Boolean disableCoupon;

    @Autowired
    private CouponCustomerService customerService;

    /**
     * 用户领取优惠券
     *
     * @param request
     * @return
     */
    @PostMapping("requestCoupon")
    public Coupon requestCoupon(@Valid @RequestBody RequestCoupon request) {
        // 如果 disableCoupon 的值为 true，则暂停领券
        if (disableCoupon) {
            log.info("暂停领取优惠券");
            return null;
        }
        return customerService.requestCoupon(request);
    }

    /**
     * 用户删除优惠券
     *
     * @param userId
     * @param couponId
     */
    @DeleteMapping("deleteCoupon")
    public void deleteCoupon(@RequestParam("userId") Long userId,
                             @RequestParam("couponId") Long couponId) {
        customerService.deleteCoupon(userId, couponId);
    }

    /**
     * 用户模拟计算每个优惠券的优惠价格
     *
     * @param order
     * @return
     */
    @PostMapping("simulateOrder")
    public SimulationResponse simulate(@Valid @RequestBody SimulationOrder order) {
        return customerService.simulateOrderPrice(order);
    }

    /**
     * 用户下单
     *
     * @param info
     * @return
     */
    @PostMapping("placeOrder")
    public ShoppingCart checkout(@Valid @RequestBody ShoppingCart info) {
        return customerService.placeOrder(info);
    }


    /**
     * 用户查询优惠券
     *
     * @param request
     * @return
     */
    @PostMapping("findCoupon")
    public List<CouponInfo> findCoupon(@Valid @RequestBody SearchCoupon request) {
        return customerService.findCoupon(request);
    }

}
