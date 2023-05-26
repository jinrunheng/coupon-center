package com.dobby.coupon.customer.dao;

import com.dobby.coupon.customer.dao.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 9:32 下午
 * @Version 1.0
 */
public interface CouponDao extends JpaRepository<Coupon, Long> {
    /**
     * 根据用户ID和Template ID，统计用户从当前优惠券模板中领了多少张券
     *
     * @param userId
     * @param templateId
     * @return
     */
    long countByUserIdAndTemplateId(Long userId, Long templateId);
}
