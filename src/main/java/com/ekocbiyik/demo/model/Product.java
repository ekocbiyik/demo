package com.ekocbiyik.demo.model;

import lombok.*;

import java.util.Arrays;
import java.util.UUID;

/**
 * ekocbiyik on 11.01.2020
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Product {

    private UUID id = UUID.randomUUID();

    @NonNull
    private String productName;

    @NonNull
    private Double unitPrize;

    @NonNull
    private Category category;

    public String getDescription() {
        return String.join(" > ", Arrays.asList(category.getDescription(), productName));
    }

}
