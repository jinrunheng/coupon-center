package com.dobby.coupon.calculation.api.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Dooby Kim
 * @Date 2023/5/23 6:46 下午
 * @Version 1.0
 * @Desc 商品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    /**
     * 商品 ID
     */
    private Long productId;

    /**
     * 商品价格
     */
    private Long price;

    /**
     * 商品在购物车的数量
     */
    private Integer count;

    /**
     * 商品销售的门店
     */
    private Long shopId;
}
