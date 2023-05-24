package com.dobby.coupon.calculation.template.impl;

import com.dobby.coupon.calculation.template.AbstractRuleTemplate;
import com.dobby.coupon.calculation.template.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 7:40 下午
 * @Version 1.0
 * @Desc 无任何折扣
 */
@Slf4j
@Component
public class NoDiscountTemplate extends AbstractRuleTemplate implements RuleTemplate {
    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        return orderTotalAmount;
    }
}
