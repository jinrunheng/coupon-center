package com.dobby.coupon.customer.feign.fallback;

import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.api.beans.SimulationOrder;
import com.dobby.coupon.calculation.api.beans.SimulationResponse;
import com.dobby.coupon.customer.feign.CalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @Author Dooby Kim
 * @Date 2023/6/20 10:28 上午
 * @Version 1.0
 * @Desc 定义 fallback 工厂方式，实现 OpenFeign 降级
 */
@Slf4j
@Component
public class CalculationServiceFallbackFactory implements FallbackFactory<CalculationService> {

    @Override
    public CalculationService create(Throwable cause) {
        return new CalculationService() {
            @Override
            public ShoppingCart checkout(ShoppingCart settlement) {
                log.info("fallback factory method test" + cause);
                return null;
            }

            @Override
            public SimulationResponse simulate(SimulationOrder simulator) {
                log.info("fallback factory method test" + cause);
                return null;
            }
        };
    }
}
