package com.ekocbiyik.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.UUID;

/**
 * ekocbiyik on 11.01.2020
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private UUID id = UUID.randomUUID();
    private Category parent;
    private String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(Category parent, String categoryName) {
        this.parent = parent;
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return parent == null ? categoryName : String.join(" > ", Arrays.asList(parent.getDescription(), categoryName));
    }

}
