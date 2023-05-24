package com.dobby.coupon.calculation.template.impl;

import com.dobby.coupon.calculation.template.AbstractRuleTemplate;
import com.dobby.coupon.calculation.template.RuleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 7:20 下午
 * @Version 1.0
 * @Desc 午夜 23 点到次日凌晨 2 点间下单，优惠金额翻倍
 */
@Slf4j
@Component
public class LonelyNightTemplate extends AbstractRuleTemplate implements RuleTemplate {
    @Override
    protected Long calculateNewPrice(Long orderTotalAmount, Long shopTotalAmount, Long quota) {
        Calendar calendar = Calendar.getInstance();
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 23 || hourOfDay < 2) {
            quota *= 2;
        }
        Long benefitAmount = shopTotalAmount < quota ? shopTotalAmount : quota;
        return orderTotalAmount - benefitAmount;
    }
}
