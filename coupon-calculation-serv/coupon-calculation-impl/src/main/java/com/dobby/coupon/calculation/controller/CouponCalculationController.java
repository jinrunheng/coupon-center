package com.dobby.coupon.calculation.controller;

import com.alibaba.fastjson.JSON;
import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.api.beans.SimulationOrder;
import com.dobby.coupon.calculation.api.beans.SimulationResponse;
import com.dobby.coupon.calculation.service.CouponCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 7:57 下午
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/calculator")
public class CouponCalculationController {

    @Autowired
    private CouponCalculationService calculationService;

    /**
     * 订单结算
     *
     * @param settlement
     * @return
     */
    @PostMapping("/checkout")
    @ResponseBody
    public ShoppingCart checkout(@RequestBody ShoppingCart settlement) {
        log.info("do calculation: {}", JSON.toJSONString(settlement));
        return calculationService.calculateOrderPrice(settlement);
    }


    /**
     * 对优惠券列表挨个试算
     * 给客户提示每个可用券的优惠额度，帮助挑选
     *
     * @param simulator
     * @return
     */
    @PostMapping("/simulate")
    @ResponseBody
    public SimulationResponse simulate(@RequestBody SimulationOrder simulator) {
        log.info("do simulation: {}", JSON.toJSONString(simulator));
        return calculationService.simulateOrder(simulator);
    }
}
