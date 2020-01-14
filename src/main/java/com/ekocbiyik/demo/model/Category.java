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
    private String categoryTitle;

    public Category(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public Category(Category parent, String categoryTitle) {
        this.parent = parent;
        this.categoryTitle = categoryTitle;
    }

    public String getDescription() {
        return parent == null ? categoryTitle : String.join(" > ", Arrays.asList(parent.getDescription(), categoryTitle));
    }

    public static Category getRootCategory(Category category) {
        return category.getParent() == null ? category : getRootCategory(category.getParent());
    }

}
