package com.ekocbiyik.demo;

import com.ekocbiyik.demo.model.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class ModelInitializeTest {

    private final Logger logger = LoggerFactory.getLogger(ModelInitializeTest.class);

    @Test
    void initCategoryTest() {

        Category giyim = new Category("Giyim");
        Category kazakHirka = new Category(giyim, "Kazak&Hırka");
        Category kazak = new Category(kazakHirka, "Kazak");
        Category hirka = new Category(kazakHirka, "Hırka");

        Assert.notNull(giyim, "Null Category!");
        Assert.notNull(kazakHirka, "Null Category!");
        Assert.notNull(kazak, "Null Category!");
        Assert.notNull(hirka, "Null Category!");

        logger.info(giyim.getDescription());
        logger.info(kazakHirka.getDescription());
        logger.info(kazak.getDescription());
        logger.info(hirka.getDescription());

        assert true;
    }

    @Test
    void initProductTest() {

        Category giyim = new Category("Giyim");
        Category kazakHirka = new Category(giyim, "Kazak&Hırka");
        Category kazak = new Category(kazakHirka, "Kazak");

        Product pcErkekKazak = new Product("Pierre Cardin Erkek Kazak", 89.97, kazak);
        Product ogErkekKazak = new Product("Oksit Giyim Erkek Kazak", 21.99, kazak);

        Assert.notNull(pcErkekKazak, "Null Product!");
        Assert.notNull(ogErkekKazak, "Null Product!");
        logger.info(pcErkekKazak.getDescription());

        assert true;
    }

    @Test
    void initCouponTest() {

        Coupon kupon1 = new Coupon(100.0, 10.0, DiscountType.RATE);
        Coupon kupon2 = new Coupon(50.0, 5.0, DiscountType.RATE);
        Coupon kupon3 = new Coupon(500.0, 50.0, DiscountType.PRICE);

        Assert.notNull(kupon1, "Null Coupon!");
        Assert.notNull(kupon2, "Null Coupon!");
        Assert.notNull(kupon3, "Null Coupon!");

        logger.info(kupon1.getDescription());
        logger.info(kupon2.getDescription());
        logger.info(kupon3.getDescription());

        assert true;
    }

    @Test
    void initCampaignTest() {

        Category giyim = new Category("Giyim");
        Category kazak = new Category(giyim, "Kazak");

        Campaign c1 = new Campaign(giyim, 20.0, 3, DiscountType.RATE);
        Campaign c2 = new Campaign(kazak, 5.0, 2, DiscountType.PRICE);

        Assert.notNull(c1, "Null Campaign!");
        Assert.notNull(c2, "Null Campaign!");

        logger.info(c1.getDescription());
        logger.info(c2.getDescription());

        assert true;
    }

}
