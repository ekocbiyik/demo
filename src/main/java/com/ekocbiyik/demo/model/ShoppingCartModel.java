package com.ekocbiyik.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ekocbiyik on 11.01.2020
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartModel {

    private UUID id = UUID.randomUUID();
    private Map<Product, Integer> productList = new HashMap<>();
    private List<Campaign> campaigns;
    private List<Coupon> coupons;
    private Double totalAmount;
    private Double couponDiscount;
    private Double campaignDiscount;
    private Double deliveryCost;
    private Double totalAmountAfterDiscounts;

}
