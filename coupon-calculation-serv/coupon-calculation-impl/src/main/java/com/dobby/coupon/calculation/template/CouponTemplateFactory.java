package com.dobby.coupon.calculation.template;

import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.template.impl.*;
import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dobby.coupon.template.api.enums.CouponType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @Author Dooby Kim
 * @Date 2023/5/24 7:38 下午
 * @Version 1.0
 * @Desc 工厂方法；根据订单中的优惠券信息，返回对应的 Template 用于计算优惠价
 */
@Component
@Slf4j
public class CouponTemplateFactory {

    @Autowired
    private MoneyOffTemplate moneyOffTemplate;

    @Autowired
    private NoDiscountTemplate noDiscountTemplate;

    @Autowired
    private DiscountTemplate discountTemplate;

    @Autowired
    private RandomReductionTemplate randomReductionTemplate;

    @Autowired
    private LonelyNightTemplate lonelyNightTemplate;

    public RuleTemplate getTemplate(ShoppingCart order) {
        // 不使用任何优惠券
        if (CollectionUtils.isEmpty(order.getCouponInfos())) {
            return noDiscountTemplate;
        }

        // 获取优惠券的类别
        // 目前每个订单只支持单张优惠券
        CouponTemplateInfo template = order.getCouponInfos().get(0).getTemplate();
        CouponType category = CouponType.convert(template.getType());

        switch (category) {
            // 订单满减券
            case MONEY_OFF:
                return moneyOffTemplate;
            // 随机立减券
            case RANDOM_DISCOUNT:
                return randomReductionTemplate;
            // 午夜下单优惠翻倍
            case LONELY_NIGHT_MONEY_OFF:
                return lonelyNightTemplate;
            // 打折券
            case DISCOUNT:
                return discountTemplate;
            // 未知类型的券模版
            default:
                return noDiscountTemplate;
        }

    }
}
