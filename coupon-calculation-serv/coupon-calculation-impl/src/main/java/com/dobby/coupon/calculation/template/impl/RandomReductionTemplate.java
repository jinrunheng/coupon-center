package com.dobby.coupon.calculation.template.impl;

import com.dobby.coupon.calculation.template.AbstractRuleTemplate;
import com.dobby.coupon.calculation.template.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 7:28 下午
 * @Version 1.0
 * @Desc 随机减钱
 */
@Slf4j
@Component
public class RandomReductionTemplate extends AbstractRuleTemplate implements RuleTemplate {
    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        Long maxBenefit = Math.min(shopTotalAmount, quota);
        int reductionAmount = new Random().nextInt(maxBenefit.intValue());
        Long newCost = orderTotalAmount - reductionAmount;

        return newCost;
    }
}
