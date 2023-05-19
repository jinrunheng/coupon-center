package com.dobby.coupon.template.dao.entity;

import com.dobby.coupon.template.api.beans.rules.TemplateRule;
import com.dobby.coupon.template.api.enums.CouponType;
import com.dobby.coupon.template.dao.converter.CouponTypeConverter;
import com.dobby.coupon.template.dao.converter.RuleConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author Dooby Kim
 * @Date 2023/5/19 6:24 下午
 * @Version 1.0
 * @Desc 优惠券模版
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
public class CouponTemplate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * 状态是否可用
     */
    @Column(name = "available", nullable = false)
    private Boolean available;

    /**
     * 使用门店；如果为空则为全门店满减券
     */
    @Column(name = "shop_id")
    private String shopId;

    /**
     * 详细信息
     */
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "type", nullable = false)
    @Convert(converter = CouponTypeConverter.class)
    private CouponType category;

    /**
     * 创建时间，通过 @CreateDate 注解自动填值（需要配合 @JpaAuditing 注解在启动类上生效）
     */
    @CreatedDate
    @Column(name = "created_time", nullable = false)
    private Date createdTime;

    /**
     * 优惠券核算规则，平铺成 JSON 字段
     */
    @Column(name = "rule", nullable = false)
    @Convert(converter = RuleConverter.class)
    private TemplateRule rule;
}