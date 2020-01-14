package com.ekocbiyik.demo.utils;

import com.ekocbiyik.demo.controller.ShoppingCart;
import com.ekocbiyik.demo.model.Category;
import com.ekocbiyik.demo.model.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        return cart.getDeliveryCost();
    }

    public static int getNumberOfDeliveries(Set<Product> productList) {
        // herhangi iki ürünün root parentı aynı ise o ürünün kategorisi aynıdır olarak varsayıldı.

        Map<Category, Integer> categoryMap = new HashMap<>();
        productList.forEach(p -> {
            Category root = Category.getRootCategory(p.getCategory());
            if (!categoryMap.containsKey(root)) {
                categoryMap.put(root, 1);
            }
        });
        return categoryMap.entrySet().stream().mapToInt(Map.Entry::getValue).sum();
    }

}
