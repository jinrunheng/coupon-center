package com.dobby.coupon.template.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Dooby Kim
 * @Date 2023/5/18 9:59 下午
 * @Version 1.0
 * @Desc 分页查找券模版
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedCouponTemplateInfo {
    public List<CouponTemplateInfo> templates;
    public int page;
    public long total;
}
