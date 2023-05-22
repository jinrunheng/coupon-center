package com.dobby.coupon.template.service;

import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dobby.coupon.template.api.beans.PagedCouponTemplateInfo;
import com.dobby.coupon.template.api.beans.TemplateSearchParams;

import java.util.Collection;
import java.util.Map;

/**
 * @Author Dooby Kim
 * @Date 2023/5/22 3:34 下午
 * @Version 1.0
 */
public interface CouponTemplateService {

    /**
     * 创建优惠券模版
     *
     * @param request
     * @return
     */
    CouponTemplateInfo createTemplate(CouponTemplateInfo request);

    /**
     * 克隆优惠券模版
     *
     * @param templateId
     * @return
     */
    CouponTemplateInfo cloneTemplate(Long templateId);

    /**
     * 模版查询（分页）
     *
     * @param request
     * @return
     */
    PagedCouponTemplateInfo search(TemplateSearchParams request);

    /**
     * 通过模版 ID 查询优惠券模版
     *
     * @param id
     * @return
     */
    CouponTemplateInfo loadTemplateInfo(Long id);

    /**
     * 使优惠券模版失效
     *
     * @param id
     */
    void deleteTemplate(Long id);

    /**
     * 批量查询
     * Map Key 为模版 ID，Value 为模版详情
     *
     * @param ids
     * @return
     */
    Map<Long, CouponTemplateInfo> getTemplateInfoMap(Collection<Long> ids);
}
