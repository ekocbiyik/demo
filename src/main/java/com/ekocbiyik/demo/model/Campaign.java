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
public class Campaign {

    private UUID id = UUID.randomUUID();

    @NonNull
    private Double discountAmount;

    @NonNull
    private DiscountType discountType;

    @NonNull
    private Category category;

    @NonNull
    private int itemCount;

    public Campaign(Category category, Double discountAmount, int itemCount, DiscountType discountType) {
        this.category = category;
        this.discountAmount = discountAmount;
        this.itemCount = itemCount;
        this.discountType = discountType;
    }

    public String getDescription() {
        return String.format("%s%s discount on category: \"%s\" if bought more than %s items!", getDiscountAmount(), getDiscountType().getValue(), getCategory(), getItemCount());
    }

}
