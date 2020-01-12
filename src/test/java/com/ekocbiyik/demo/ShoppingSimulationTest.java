package com.ekocbiyik.demo;

import com.ekocbiyik.demo.controller.ShoppingCart;
import com.ekocbiyik.demo.exceptions.DemoException;
import com.ekocbiyik.demo.model.*;
import com.ekocbiyik.demo.utils.DeliveryCostUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * ekocbiyik on 11.01.2020
 */

@SpringBootTest
public class ShoppingSimulationTest {

    private Map<String, Category> categoryMap = new HashMap<>();
    private Map<String, Product> productMap = new HashMap<>();
    private ShoppingCart cart;

    @BeforeEach
    public void initializeItems() {

        // kategoriler
        Category giyim = new Category("Giyim");
        Category kazakHirka = new Category(giyim, "Kazak&Hırka");
        Category kazak = new Category(kazakHirka, "Kazak");
        Category hirka = new Category(kazakHirka, "Hırka");

        Category ayakkabi = new Category("Ayakkabi");
        Category sporAyakkabi = new Category(ayakkabi, "Spor Ayakkabi");
        Category yuruyusAyakkabisi = new Category(sporAyakkabi, "Yürüyüş Ayakkabısı");
        Category futbolAyakkabisi = new Category(sporAyakkabi, "Futbol Ayakkabısı");

        categoryMap.put("giyim", giyim);
        categoryMap.put("kazakHirka", kazakHirka);
        categoryMap.put("kazak", kazak);
        categoryMap.put("hirka", hirka);
        categoryMap.put("ayakkabi", ayakkabi);
        categoryMap.put("sporAyakkabi", sporAyakkabi);
        categoryMap.put("yuruyusAyakkabisi", yuruyusAyakkabisi);
        categoryMap.put("futbolAyakkabisi", futbolAyakkabisi);


        // shoppingWithoutAnyDiscountTest
        productMap.put("dfctErkekKazak", new Product("Defacto Erkek Kazak", 39.99, kazak));
        productMap.put("usErkekHirka", new Product("US Polo Ass Hırka", 124.95, hirka));
        productMap.put("mpKrmpn", new Product("M.P Krampon", 51.50, futbolAyakkabisi));

        // shoppingWithCampaignDiscountTest
        productMap.put("pcErkekKazak", new Product("Pierre Cardin Erkek Kazak", 70.0, kazak));
        productMap.put("lcwErkekHirka", new Product("LCW Erkek Hırka", 50.0, hirka));
        productMap.put("pumaAykb", new Product("Puma Siyah Ayakkabı", 350.0, yuruyusAyakkabisi));


//        productMap.put("nikeAykb", new Product("NIKE Ayakkabı", 479.88, yuruyusAyakkabisi));
//        productMap.put("polarisAykb", new Product("Polaris Ayakkabı", 34.99, yuruyusAyakkabisi));
//        productMap.put("walkedKrmpn", new Product("Walked Krampon", 59.90, futbolAyakkabisi));
    }

    @Test
    public void shoppingWithoutAnyDiscountTest() throws DemoException {
        cart = new ShoppingCart();
        cart.addItem(productMap.get("dfctErkekKazak"), 1);
        cart.addItem(productMap.get("usErkekHirka"), 1);
        cart.addItem(productMap.get("mpKrmpn"), 1);

        DeliveryCostUtils delivery = new DeliveryCostUtils(12.0, 10.0, 2.99);
        delivery.calculateFor(cart);

        assert cart.getTotalAmountAfterDiscounts().equals(216.44);
    }

    @Test
    public void shoppingWithCampaignRateDiscountTest() throws DemoException {
        cart = new ShoppingCart();
        cart.addItem(productMap.get("pcErkekKazak"), 2);
        cart.addItem(productMap.get("lcwErkekHirka"), 2);
        cart.addItem(productMap.get("pumaAykb"), 1);

        // kamp1: giyim kategorisinde 2 ürün alana %25 indirm
        Campaign campaign1 = new Campaign(categoryMap.get("giyim"), 25.0, 2, DiscountType.RATE);

        // kamp2: hırka kategorisinde 2 ürüne 20 TL indirim
        Campaign campaign2 = new Campaign(categoryMap.get("hirka"), 20.0, 2, DiscountType.PRICE);

        // kamp3: yürüyüş ayakkabısında 1 üründe %30 indirim
        Campaign campaign3 = new Campaign(categoryMap.get("yuruyusAyakkabisi"), 30.0, 1, DiscountType.RATE);

        cart.applyDiscount(campaign1, campaign2, campaign3);

        DeliveryCostUtils delivery = new DeliveryCostUtils(12.0, 10.0, 2.99);
        delivery.calculateFor(cart);

        assert cart.getTotalAmountAfterDiscounts().equals(425.0);
    }

    @Test
    public void shoppingWithCouponDiscountTest() throws DemoException {
        cart = new ShoppingCart();
        cart.addItem(productMap.get("pcErkekKazak"), 2);
        cart.addItem(productMap.get("lcwErkekHirka"), 2);
        cart.addItem(productMap.get("pumaAykb"), 1);

        // kamp1: min 100 TL alışverişe %10 indirim
        Coupon coupon1 = new Coupon(100.0, 40.0, DiscountType.RATE);
        cart.applyCoupon(coupon1);

        Coupon coupon2 = new Coupon(200.0, 100.0, DiscountType.PRICE);
        cart.applyCoupon(coupon2);

        DeliveryCostUtils delivery = new DeliveryCostUtils(12.0, 10.0, 2.99);
        delivery.calculateFor(cart);

        assert cart.getTotalAmountAfterDiscounts().equals(490.0);
    }

}
