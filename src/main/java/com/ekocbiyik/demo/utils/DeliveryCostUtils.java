package com.ekocbiyik.demo.utils;

import com.ekocbiyik.demo.controller.ShoppingCart;

/**
 * ekocbiyik on 11.01.2020
 */

public class DeliveryCostUtils {

    private Double costPerDelivery;
    private Double costPerProduct;
    private Double fixedCost;

    public DeliveryCostUtils(Double costPerDelivery, Double costPerProduct, Double fixedCost) {
        this.costPerDelivery = costPerDelivery;
        this.costPerProduct = costPerProduct;
        this.fixedCost = fixedCost;
    }

    public Double calculateFor(ShoppingCart cart) {
        cart.setDeliveryCost((costPerDelivery * cart.getNumberOfDeliveries()) + (costPerProduct * cart.getNumberOfProducts()) + fixedCost);
        cart.print();
        return cart.getDeliveryCost();
    }
}
