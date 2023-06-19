package com.dobby.coupon.customer.service.impl;

import com.dobby.coupon.calculation.api.beans.ShoppingCart;
import com.dobby.coupon.calculation.api.beans.SimulationOrder;
import com.dobby.coupon.calculation.api.beans.SimulationResponse;
import com.dobby.coupon.customer.dao.CouponDao;
import com.dobby.coupon.customer.dao.entity.Coupon;
import com.dobby.coupon.customer.feign.CalculationService;
import com.dobby.coupon.customer.feign.TemplateService;
import com.dobby.coupon.customer.service.CouponConverter;
import com.dobby.coupon.customer.service.CouponCustomerService;
import com.dobby.coupon.template.api.beans.CouponInfo;
import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dooby.coupon.customer.api.beans.RequestCoupon;
import com.dooby.coupon.customer.api.beans.SearchCoupon;
import com.dooby.coupon.customer.api.enums.CouponStatus;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author Dooby Kim
 * @Date 2023/5/28 3:46 下午
 * @Version 1.0
 */
@Slf4j
@Service
public class CouponCustomerServiceImpl implements CouponCustomerService {

    @Autowired
    private CouponDao couponDao;

    // 将 webClient 调用，改造为 openFeign 调用
//    @Autowired
//    private WebClient.Builder webClientBuilder;

    @Resource
    private TemplateService templateService;

    @Resource
    private CalculationService calculationService;

//    @Autowired
//    private CouponTemplateService templateService;
//
//    @Autowired
//    private CouponCalculationService calculationService;

    /**
     * 用户领取优惠券
     *
     * @param request
     * @return
     */
    @Override
    public Coupon requestCoupon(RequestCoupon request) {
        // final CouponTemplateInfo templateInfo = templateService.loadTemplateInfo(request.getCouponTemplateId());
//        final CouponTemplateInfo templateInfo = webClientBuilder.build()
//                .get()
//                .uri("http://coupon-template-serv/template/getTemplate?id=" + request.getCouponTemplateId())
//                .header(Constant.TRAFFIC_VERSION, request.getTrafficVersion())
//                .retrieve()
//                .bodyToMono(CouponTemplateInfo.class)
//                .block();
        // 20230619 改造为 openFeign 调用
        final CouponTemplateInfo templateInfo = templateService.getTemplate(request.getCouponTemplateId());

        // 如果模版不存在则报错
        if (templateInfo == null) {
            log.error("invalid template id = {}", request.getCouponTemplateId());
            throw new IllegalArgumentException("invalid template id");
        }

        // 校验模版是否过期
        final Long expireTime = templateInfo.getRule().getDeadline();
        if (expireTime != null && Calendar.getInstance().getTimeInMillis() >= expireTime || BooleanUtils.isFalse(templateInfo.getAvailable())) {
            log.error("template is not available id = {}", request.getCouponTemplateId());
            throw new IllegalArgumentException("template is not available");
        }

        // 校验用户的领券数量是否超过上限
        final long count = couponDao.countByUserIdAndTemplateId(request.getUserId(), request.getCouponTemplateId());
        if (count >= templateInfo.getRule().getLimitation()) {
            log.error("exceeds maximum number");
            throw new IllegalArgumentException("exceeds maximum number");
        }

        final Coupon coupon = Coupon.builder()
                .templateId(request.getCouponTemplateId())
                .userId(request.getUserId())
                .shopId(templateInfo.getShopId())
                .status(CouponStatus.AVAILABLE)
                .build();

        couponDao.save(coupon);
        return coupon;
    }

    /**
     * 用户下单
     *
     * @param order
     * @return
     */
    @Override
    @Transactional
    public ShoppingCart placeOrder(ShoppingCart order) {
        if (CollectionUtils.isEmpty(order.getProducts())) {
            log.error("shopping cart is empty, order : {}", order);
            throw new IllegalArgumentException("shopping cart is empty");
        }

        Coupon coupon = null;
        if (order.getCouponId() != null) {
            // 如果用户有优惠券，则验证是否可用
            final Coupon example = Coupon.builder()
                    .userId(order.getUserId())
                    .id(order.getCouponId())
                    .status(CouponStatus.AVAILABLE)
                    .build();

            coupon = couponDao.findAll(Example.of(example))
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Coupon not found"));

            final CouponInfo couponInfo = CouponConverter.convertToCoupon(coupon);
            // final CouponTemplateInfo templateInfo = templateService.loadTemplateInfo(couponInfo.getTemplateId());
            // 改造 webflux 调用
//            final CouponTemplateInfo templateInfo = loadTemplateInfo(couponInfo.getTemplateId());
            // 20230619 改造为 openFeign 调用
            final CouponTemplateInfo templateInfo = templateService.getTemplate(couponInfo.getTemplateId());
            couponInfo.setTemplate(templateInfo);
            order.setCouponInfos(Lists.newArrayList(couponInfo));
        }

        // order 结算
        // final ShoppingCart checkout = calculationService.calculateOrderPrice(order);
        // 改造 webflux 调用
//        final ShoppingCart checkout = webClientBuilder.build().post()
//                .uri("http://coupon-calculation-serv/calculator/checkout")
//                .bodyValue(order)
//                .retrieve()
//                .bodyToMono(ShoppingCart.class)
//                .block();
        // 20230619 改造为 openFeign 调用
        final ShoppingCart checkout = calculationService.checkout(order);

        if (coupon != null) {
            // 如果优惠券没有被结算掉，而用户传递了优惠券，则提示该订单不满足优惠条件，无法使用优惠券
            if (CollectionUtils.isEmpty(checkout.getCouponInfos())) {
                log.error("cannot apply coupon to order, couponId : {}", coupon.getId());
                throw new IllegalArgumentException("coupon is not applicable to this order");
            }

            log.info("update coupon status to used, couponId = {}", coupon.getId());
            coupon.setStatus(CouponStatus.USED);
            couponDao.save(coupon);
        }

        return checkout;
    }


    /**
     * 优惠券试算
     *
     * @param order
     * @return
     */
    @Override
    public SimulationResponse simulateOrderPrice(SimulationOrder order) {

        List<CouponInfo> couponInfos = Lists.newArrayList();

        for (Long couponId : order.getCouponIDs()) {
            final Coupon example = Coupon.builder()
                    .userId(order.getUserId())
                    .id(couponId)
                    .status(CouponStatus.AVAILABLE)
                    .build();
            final Optional<Coupon> couponOptional = couponDao.findAll(Example.of(example))
                    .stream()
                    .findFirst();

            // 加载优惠券模版信息
            if (couponOptional.isPresent()) {
                final Coupon coupon = couponOptional.get();
                final CouponInfo couponInfo = CouponConverter.convertToCoupon(coupon);
                // final CouponTemplateInfo couponTemplateInfo = templateService.loadTemplateInfo(couponInfo.getTemplateId());
                // 改造 webflux 调用
//                final CouponTemplateInfo couponTemplateInfo = loadTemplateInfo(couponInfo.getTemplateId());
                // 20230619 改造为 openFeign 调用
                final CouponTemplateInfo couponTemplateInfo = templateService.getTemplate(couponInfo.getTemplateId());
                couponInfo.setTemplate(couponTemplateInfo);
                couponInfos.add(couponInfo);
            }
        }
        order.setCouponInfos(couponInfos);

        // 调用接口试算微服务
        // return calculationService.simulateOrder(order);
        // 改造 webflux 调用
//        return webClientBuilder.build()
//                .post()
//                .uri("http://coupon-calculation-serv/calculator/simulate")
//                .bodyValue(order)
//                .retrieve()
//                .bodyToMono(SimulationResponse.class)
//                .block();
        // 20230619 改造为 openFeign 调用
        return calculationService.simulate(order);
    }

    /**
     * 逻辑删除优惠券
     *
     * @param userId
     * @param couponId
     */
    @Override
    public void deleteCoupon(Long userId, Long couponId) {
        final Coupon example = Coupon.builder()
                .userId(userId)
                .id(couponId)
                .status(CouponStatus.AVAILABLE)
                .build();

        final Coupon coupon = couponDao.findAll(Example.of(example))
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find available coupon"));

        coupon.setStatus(CouponStatus.INACTIVE);
        couponDao.save(coupon);
    }

    /**
     * 用户查询优惠券
     *
     * @param request
     * @return
     */
    @Override
    public List<CouponInfo> findCoupon(SearchCoupon request) {

        final Coupon example = Coupon.builder()
                .userId(request.getUserId())
                .status(CouponStatus.convert(request.getCouponStatus()))
                .shopId(request.getShopId())
                .build();

        final List<Coupon> coupons = couponDao.findAll(Example.of(example));
        if (coupons.isEmpty()) {
            return Lists.newArrayList();
        }

        final List<Long> templateIds = coupons.stream()
                .map(Coupon::getTemplateId)
                .collect(Collectors.toList());

        // final Map<Long, CouponTemplateInfo> templateInfoMap = templateService.getTemplateInfoMap(templateIds);
        // 改造为 webflux 调用
//        final Map<Long, CouponTemplateInfo> templateInfoMap = webClientBuilder.build()
//                .get()
//                .uri("http://coupon-template-serv/template/getBatch?ids=" + templateIds)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<Map<Long, CouponTemplateInfo>>() {
//                })
//                .block();
        // 20230619 改造为 openFeign 调用
        final Map<Long, CouponTemplateInfo> templateInfoMap = templateService.getTemplateInBatch(templateIds);

        coupons.forEach(e -> e.setTemplateInfo(templateInfoMap.get(e.getTemplateId())));

        return coupons.stream()
                .map(CouponConverter::convertToCoupon)
                .collect(Collectors.toList());
    }

//    private CouponTemplateInfo loadTemplateInfo(Long templateId) {
//        return webClientBuilder.build().get()
//                .uri("http://coupon-template-serv/template/getTemplate?id=" + templateId)
//                .retrieve()
//                .bodyToMono(CouponTemplateInfo.class)
//                .block();
//    }
}
