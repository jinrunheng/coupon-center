package com.dobby.coupon.template.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dobby.coupon.template.api.beans.PagedCouponTemplateInfo;
import com.dobby.coupon.template.api.beans.TemplateSearchParams;
import com.dobby.coupon.template.service.CouponTemplateService;
import com.google.common.collect.Maps;
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
    @PostMapping("/addTemplate")
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
     * 读取优惠券
     *
     * @param id
     * @return
     */
    @GetMapping("/getTemplate")
    @SentinelResource(value = "getTemplate")
    public CouponTemplateInfo getTemplate(@RequestParam("id") Long id) {
        log.info("load template, id : {}", id);
        return couponTemplateService.loadTemplateInfo(id);
    }

    /**
     * 批量获取
     *
     * @param ids
     * @return
     */
    @GetMapping("/getBatch")
    // 你也可以通过defaultFallback属性做一个全局限流、降级的处理逻辑
    // 如果你不想将降级方法写在当前类里，可以通过blockHandlerClass和fallbackClass指定"降级类"
    @SentinelResource(value = "getTemplateInBatch",
            fallback = "getTemplateInBatch_fallback",
            blockHandler = "getTemplateInBatch_block")
    public Map<Long, CouponTemplateInfo> getTemplateInBatch(@RequestParam("ids") Collection<Long> ids) {
        log.info("getTemplateInBatch: {}", JSON.toJSONString(ids));
        // 可以测试异常比例、异常数熔断
//        if (ids.size() == 2) {
//            throw new RuntimeException("异常");
//        }

        // 可以测试慢调用熔断
//            try {
//                Thread.sleep(500 * ids.size());
//            } catch (Exception e) {
//            }

        return couponTemplateService.getTemplateInfoMap(ids);
    }

    /**
     * 接口被降级时的方法
     *
     * @param ids
     * @return
     */
    public Map<Long, CouponTemplateInfo> getTemplateInBatch_fallback(Collection<Long> ids) {
        log.info("接口被降级");
        return Maps.newHashMap();
    }

    /**
     * 流控降级的方法
     *
     * @param ids
     * @param e
     * @return
     */
    public Map<Long, CouponTemplateInfo> getTemplateInBatch_block(
            Collection<Long> ids, BlockException e) {
        log.info("接口被限流");
        return Maps.newHashMap();
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
