package com.dobby.coupon.calculation.template.impl;

import com.dobby.coupon.calculation.template.AbstractRuleTemplate;
import com.dobby.coupon.calculation.template.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 7:15 下午
 * @Version 1.0
 * @Desc 打折优惠券
 */
@Slf4j
@Component
public class DiscountTemplate extends AbstractRuleTemplate implements RuleTemplate {
    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        Long newPrice = convertToDecimal(shopTotalAmount * (quota.doubleValue() / 100));
        log.debug("original price={}, new price={}", orderTotalAmount, newPrice);
        return newPrice;
    }
}
