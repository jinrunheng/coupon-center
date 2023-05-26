package com.dobby.coupon.customer.dao.entity;

import com.dobby.coupon.customer.dao.convertor.CouponStatusConverter;
import com.dobby.coupon.template.api.beans.CouponTemplateInfo;
import com.dooby.coupon.customer.api.enums.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 6:52 下午
 * @Version 1.0
 * @Desc 优惠券
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 对应模版 ID
     */
    @Column(name = "template_id", nullable = false)
    private Long templateId;

    /**
     * 拥有这张优惠券的用户 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 冗余一个 shop方便查找
     */
    @Column(name = "shop_id")
    private Long shopId;


    @Column(name = "status", nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;

    /**
     * 被 Transient 标记的属性是不会被持久化的
     */
    @Transient
    private CouponTemplateInfo templateInfo;

    /**
     * 获取时间自动生成
     */
    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private Date createTime;
}
