package com.dobby.coupon.customer.dao.convertor;

import com.dooby.coupon.customer.api.enums.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @Author Dooby Kim
 * @Date 2023/5/26 9:16 下午
 * @Version 1.0
 * @Desc
 */
@Converter
public class CouponStatusConverter implements AttributeConverter<CouponStatus, Integer> {

    /**
     * enum 转 DB
     *
     * @param couponStatus
     * @return
     */
    @Override
    public Integer convertToDatabaseColumn(CouponStatus couponStatus) {
        return couponStatus.getCode();
    }

    /**
     * DB 转 enum
     *
     * @param code
     * @return
     */
    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.convert(code);
    }
}
