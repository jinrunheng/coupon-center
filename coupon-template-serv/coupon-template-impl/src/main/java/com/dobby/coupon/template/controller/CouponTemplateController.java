package com.dobby.coupon.template.controller;

import com.alibaba.fastjson.JSON;
import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dobby.coupon.template.api.beans.PagedCouponTemplateInfo;
import com.dobby.coupon.template.api.beans.TemplateSearchParams;
import com.dobby.coupon.template.service.CouponTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

/**
 * @Author Dooby Kim
 * @Date 2023/5/22 10:30 下午
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/template")
public class CouponTemplateController {

    @Autowired
    private CouponTemplateService couponTemplateService;

    /**
     * 创建优惠券模版
     *
     * @param request
     * @return
     */
    @PostMapping("")
    public CouponTemplateInfo addTemplate(@Valid @RequestBody CouponTemplateInfo request) {
        log.info("create coupon template : data = {}", request);
        return couponTemplateService.createTemplate(request);
    }

    /**
     * 克隆优惠券模版
     *
     * @param templateId
     * @return
     */
    @PostMapping("/cloneTemplate")
    public CouponTemplateInfo cloneTemplate(@RequestParam("id") Long templateId) {
        log.info("clone coupon template: data={}", templateId);
        return couponTemplateService.cloneTemplate(templateId);
    }

    /**
     * 批量获取
     *
     * @param ids
     * @return
     */
    @GetMapping("/getBatch")
    public Map<Long, CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids) {
        log.info("getTemplateInBatch: {}", JSON.toJSONString(ids));
        return couponTemplateService.getTemplateInfoMap(ids);
    }

    /**
     * 搜索模版
     *
     * @param request
     * @return
     */
    @PostMapping("/search")
    public PagedCouponTemplateInfo search(@Valid @RequestBody TemplateSearchParams request) {
        log.info("search templates, payload={}", request);
        return couponTemplateService.search(request);
    }

    /**
     * 使优惠券模版无效
     *
     * @param id
     */
    @DeleteMapping("/deleteTemplate")
    public void deleteTemplate(@RequestParam("id") Long id) {
        log.info("Load template, id={}", id);
        couponTemplateService.deleteTemplate(id);
    }
}
