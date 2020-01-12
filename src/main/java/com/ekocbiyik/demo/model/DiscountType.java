package com.ekocbiyik.demo.model;

/**
 * ekocbiyik on 11.01.2020
 */
public enum DiscountType {

    RATE("%"),
    PRICE("TL");

    String value;

    DiscountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
