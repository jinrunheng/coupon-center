package com.dobby.coupon.template.api.beans.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dooby Kim
 * @Date 2023/5/18 9:34 下午
 * @Version 1.0
 * @Desc 优惠券模版规则，其包含了两个规则：一是领券规则，包括每个用户可领取优惠券的数量和券模版的过期时间；二是券模版的计算规则
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRule {
    /**
     * 可享受的折扣
     */
    private Discount discount;

    /**
     * 每个人最多可以领券的数量
     */
    private Integer limitation;

    /**
     * 券过期时间
     */
    private Long deadline;
}
