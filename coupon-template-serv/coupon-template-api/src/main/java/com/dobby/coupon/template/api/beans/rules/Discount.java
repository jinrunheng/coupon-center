package com.dobby.coupon.template.api.beans.rules;

/**
 * @Author Dooby Kim
 * @Date 2023/5/18 9:38 下午
 * @Version 1.0
 * @Desc 定义了使用优惠券的折扣规则
 */
public class Discount {

    /**
     * 1. 对于满减券 - quota是减掉的钱数，单位是分
     * 2. 对于打折券 - quota是折扣(以100表示原价)，90就是打9折, 95就是95折
     * 3. 对于随机立减券 - quota是最高的随机立减额
     * 4. 对于晚间特别优惠券 - quota是日间优惠额，晚间优惠翻倍
     */
    private Long quota;

    /**
     * 订单最低要达到多少钱才能用优惠券，单位为分
     */
    private Long threshold;
}
