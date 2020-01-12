package com.ekocbiyik.demo.controller;

import com.ekocbiyik.demo.exceptions.DemoException;
import com.ekocbiyik.demo.model.Campaign;
import com.ekocbiyik.demo.model.Coupon;
import com.ekocbiyik.demo.model.Product;

/**
 * ekocbiyik on 11.01.2020
 */
public interface IShoppingCartController {

    void addItem(Product product, Integer quantity) throws DemoException;

    void applyDiscount(Campaign... campaigns) throws DemoException;

    void applyCoupon(Coupon coupon) throws DemoException;

    Double getTotalAmountAfterDiscounts();

    Double getCouponDiscount();

    Double getCampaignDiscount();

    Double getDeliveryCost();

    void setDeliveryCost(Double deliveryCost);

    void print();

}
