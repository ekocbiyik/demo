package com.ekocbiyik.demo.model;

import lombok.*;

import java.util.UUID;

/**
 * ekocbiyik on 11.01.2020
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    private UUID id = UUID.randomUUID();

    @NonNull
    private Double discountAmount;

    @NonNull
    private DiscountType discountType;

    @NonNull
    private Double minPurchase;

    public Coupon(Double minPurchase, Double discountAmount, DiscountType discountType) {
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.minPurchase = minPurchase;
    }

    public String getDescription() {
        return String.format("%s%s discount for %s TL min purchase!", getMinPurchase(), getDiscountAmount(), getDiscountType().getValue());
    }

}
