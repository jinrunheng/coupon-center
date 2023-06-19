package com.dobby.coupon.customer.feign;

import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.api.beans.SimulationOrder;
import com.dobby.coupon.calculation.api.beans.SimulationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Dooby Kim
 * @Date 2023/6/19 10:07 上午
 * @Version 1.0
 * @Desc OpenFeign 用于实现对 coupon-calculation-serv 的远程调用代理
 */
@FeignClient(value = "coupon-calculation-serv", path = "/calculator")
public interface CalculationService {

    /**
     * 订单结算
     *
     * @param settlement
     * @return
     */
    @PostMapping("/checkout")
    @ResponseBody
    ShoppingCart checkout(@RequestBody ShoppingCart settlement);

    /**
     * 对优惠券列表挨个试算
     *
     * @param simulator
     * @return
     */
    @PostMapping("/simulate")
    @ResponseBody
    SimulationResponse simulate(@RequestBody SimulationOrder simulator);
}
