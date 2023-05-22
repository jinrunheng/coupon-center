package com.dobby.coupon.template.service.impl;

import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dobby.coupon.template.api.beans.PagedCouponTemplateInfo;
import com.dobby.coupon.template.api.beans.TemplateSearchParams;
import com.dobby.coupon.template.api.enums.CouponType;
import com.dobby.coupon.template.dao.CouponTemplateDao;
import com.dobby.coupon.template.dao.entity.CouponTemplate;
import com.dobby.coupon.template.service.CouponTemplateService;
import com.dobby.coupon.template.service.converter.CouponTemplateConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Dooby Kim
 * @Date 2023/5/22 5:11 下午
 * @Version 1.0
 */
@Slf4j
@Service
public class CouponTemplateServiceImpl implements CouponTemplateService {

    @Autowired
    private CouponTemplateDao couponTemplateDao;

    /**
     * 创建优惠券模版，每个门店最多可以创建 100 张优惠券模版
     *
     * @param request
     * @return
     */
    @Override
    public CouponTemplateInfo createTemplate(CouponTemplateInfo request) {
        if (request.getShopId() != null) {
            Integer count = couponTemplateDao.countByShopIdAndAvailable(request.getShopId(), true);
            if (count >= 100) {
                log.error("已达到优惠券模版数量上限！");
                throw new UnsupportedOperationException("已达到优惠券模版数量上限！");
            }
        }

        // 创建优惠券
        CouponTemplate template = CouponTemplate.builder()
                .name(request.getName())
                .available(true)
                .description(request.getDesc())
                .category(CouponType.convert(request.getType()))
                .shopId(request.getShopId())
                .rule(request.getRule())
                .build();

        final CouponTemplate save = couponTemplateDao.save(template);
        return CouponTemplateConverter.convertToTemplateInfo(save);

    }

    /**
     * 克隆优惠券模版
     *
     * @param templateId
     * @return
     */
    @Override
    public CouponTemplateInfo cloneTemplate(Long templateId) {

        CouponTemplate template = couponTemplateDao.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Template ID"));

        CouponTemplate target = new CouponTemplate();
        BeanUtils.copyProperties(template, target);
        target.setAvailable(true);
        target.setId(null);

        couponTemplateDao.save(target);
        return CouponTemplateConverter.convertToTemplateInfo(target);
    }

    @Override
    public PagedCouponTemplateInfo search(TemplateSearchParams request) {
        final CouponTemplate example = CouponTemplate.builder()
                .shopId(request.getShopId())
                .category(CouponType.convert(request.getType()))
                .available(request.getAvailable())
                .name(request.getName())
                .build();

        Pageable page = PageRequest.of(request.getPage(), request.getPageSize());
        Page<CouponTemplate> result = couponTemplateDao.findAll(Example.of(example), page);
        final List<CouponTemplateInfo> collect = result.stream()
                .map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toList());

        final PagedCouponTemplateInfo response = PagedCouponTemplateInfo.builder()
                .templates(collect)
                .page(request.getPage())
                .total(result.getTotalElements())
                .build();

        return response;
    }

    /**
     * 通过 ID 查询优惠券模版
     *
     * @param id
     * @return
     */
    @Override
    public CouponTemplateInfo loadTemplateInfo(Long id) {
        Optional<CouponTemplate> template = couponTemplateDao.findById(id);
        return template.map(CouponTemplateConverter::convertToTemplateInfo).orElse(null);
    }

    /**
     * 使优惠券模版无效
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        final int row = couponTemplateDao.makeCouponUnavailable(id);
        if (row == 0) {
            throw new IllegalArgumentException("Template not found : " + id);
        }
    }

    /**
     * 批量读取模版
     *
     * @param ids
     * @return
     */
    @Override
    public Map<Long, CouponTemplateInfo> getTemplateInfoMap(Collection<Long> ids) {
        List<CouponTemplate> templates = couponTemplateDao.findAllById(ids);
        final Map<Long, CouponTemplateInfo> response = templates.stream()
                .map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toMap(CouponTemplateInfo::getId, Function.identity()));

        return response;
    }


    public List<CouponTemplateInfo> searchTemplate(CouponTemplateInfo request) {
        final CouponTemplate example = CouponTemplate.builder()
                .shopId(request.getShopId())
                .category(CouponType.convert(request.getType()))
                .available(request.getAvailable())
                .name(request.getName())
                .build();

        List<CouponTemplate> result = couponTemplateDao.findAll(Example.of(example));

        final List<CouponTemplateInfo> response = result.stream()
                .map(CouponTemplateConverter::convertToTemplateInfo)
                .collect(Collectors.toList());

        return response;

    }
}
