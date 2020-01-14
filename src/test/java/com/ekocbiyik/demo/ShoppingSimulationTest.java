package com.ekocbiyik.demo;

import com.ekocbiyik.demo.controller.ShoppingCart;
import com.ekocbiyik.demo.exceptions.DemoException;
import com.ekocbiyik.demo.model.*;
import com.ekocbiyik.demo.utils.DeliveryCostUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

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
    }

    @Test
    public void shoppingWithoutAnyDiscountTest() throws DemoException {
        cart = new ShoppingCart();
        cart.addItem(productMap.get("dfctErkekKazak"), 1);  // 39.99
        cart.addItem(productMap.get("usErkekHirka"), 1);    // 124.95
        cart.addItem(productMap.get("mpKrmpn"), 1);         // 51.50

        DeliveryCostUtils delivery = new DeliveryCostUtils(12.0, 10.0, 2.99);
        delivery.calculateFor(cart);
        cart.print();

        /*
         * toplam tutar     216.44
         * kargo            56.99
         * total            273.43
         */

        assert cart.getCost().equals(273.43);
    }

    @Test
    public void shoppingWithCampaignRateDiscountTest() throws DemoException {
        cart = new ShoppingCart();
        cart.addItem(productMap.get("pcErkekKazak"), 2);    // 70.0 x 2 = 140.0
        cart.addItem(productMap.get("lcwErkekHirka"), 2);   // 50.0 x 2 = 100.0
        cart.addItem(productMap.get("pumaAykb"), 1);        // 350.0 x 1 = 350

        // kamp1: giyim kategorisinde 2 ürün alana %25 indirm
        Campaign campaign1 = new Campaign(categoryMap.get("giyim"), 25.0, 2, DiscountType.RATE);

        // kamp2: hırka kategorisinde 2 ürüne 20 TL indirim
        Campaign campaign2 = new Campaign(categoryMap.get("hirka"), 20.0, 2, DiscountType.PRICE);

        // kamp3: yürüyüş ayakkabısında 1 üründe %30 indirim
        Campaign campaign3 = new Campaign(categoryMap.get("yuruyusAyakkabisi"), 30.0, 1, DiscountType.RATE);

        cart.applyDiscount(campaign1, campaign2, campaign3);

        DeliveryCostUtils delivery = new DeliveryCostUtils(12.0, 10.0, 2.99);
        delivery.calculateFor(cart);
        cart.print();

        /*
         * toplam tutar  590 tl
         * 1.kampanyadan -60 tl
         * 2.kampanyadan -20 tl
         * 3.kampanyadan -105 tl
         * total indirim 165
         * kargo tutarı  56.99
         *
         * total         461.99
         *
         */

        assert cart.getCost().equals(461.99);
    }

    @Test
    public void shoppingWithCouponDiscountTest() throws DemoException {
        cart = new ShoppingCart();
        cart.addItem(productMap.get("pcErkekKazak"), 2);
        cart.addItem(productMap.get("lcwErkekHirka"), 2);
        cart.addItem(productMap.get("pumaAykb"), 1);

        // kamp1: min 100 TL alışverişe %40 indirim
        Coupon coupon1 = new Coupon(100.0, 40.0, DiscountType.RATE);
        cart.applyCoupon(coupon1);

        Coupon coupon2 = new Coupon(200.0, 100.0, DiscountType.PRICE);
        cart.applyCoupon(coupon2);

        Coupon coupon3 = new Coupon(100.0, 100.0, DiscountType.PRICE);
        cart.applyCoupon(coupon3);

        DeliveryCostUtils delivery = new DeliveryCostUtils(12.0, 10.0, 2.99);
        delivery.calculateFor(cart);
        cart.print();

        assert cart.getCost().equals(546.99);
    }

    @Test
    public void sortCouponsTest() {

        /*
         * kuponları max. sağladığı indirim tutarından min. doğru sıralar.
         */

        Double totalAmount = 300.0;

        Set<Coupon> couponList = new HashSet<>();
        Map<Coupon, Double> calculatedCoupons = new HashMap<>();

        Coupon coupon1 = new Coupon(100.0, 10.0, DiscountType.RATE);
        couponList.add(coupon1); // 30 tl indirim

        Coupon coupon2 = new Coupon(200.0, 100.0, DiscountType.PRICE);
        couponList.add(coupon2); // 100 tl indirim

        Coupon coupon3 = new Coupon(300.0, 120.0, DiscountType.PRICE);
        couponList.add(coupon3); // 120 tl indirim

        for (Coupon c : couponList) {
            if (c.getDiscountType() == DiscountType.RATE) {
                calculatedCoupons.put(c, totalAmount * c.getDiscountAmount() / 100);
            }

            if (c.getDiscountType() == DiscountType.PRICE) {
                calculatedCoupons.put(c, c.getDiscountAmount());
            }
        }

        Set<Coupon> sortedCoupons = calculatedCoupons.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new))
                .keySet();

        List<Coupon> finalCoupons = new ArrayList<>();
        finalCoupons.addAll(sortedCoupons);
        Collections.reverse(finalCoupons);
        finalCoupons.forEach(c -> System.out.println(c.getDiscountAmount()));

        assert calculatedCoupons.get(finalCoupons.get(0)) > calculatedCoupons.get(finalCoupons.get(finalCoupons.size() - 1));
    }

    @Test
    public void shoppingSameCategoryCampaignDiscountTest() throws DemoException {

        Category catGiyim = new Category("Giyim");
        Category catKazakHirka = new Category(catGiyim, "Kazak&Hırka");
        Category catKazak = new Category(catKazakHirka, "Kazak");
        Category catHirka = new Category(catKazakHirka, "Hırka");

        Product pKazak = new Product("Defacto Erkek Kazak", 70.0, catKazak);
        Product pHirka = new Product("US Polo Ass Hırka", 50.0, catHirka);

        // kamp1: kazak kategorisinde 2 ürün alana %25 indirm
        Campaign campaign1 = new Campaign(catKazak, 25.0, 2, DiscountType.RATE);

        // kamp2: hırka kategorisinde 2 ürün alana 20 TL indirim
        Campaign campaign2 = new Campaign(catHirka, 20.0, 2, DiscountType.PRICE);

        ShoppingCart sepet = new ShoppingCart();
        sepet.applyDiscount(campaign1, campaign2);

//        sepet.print();

        sepet.addItem(pKazak, 1);
//        sepet.print();

        sepet.addItem(pKazak, 1);
//        sepet.print();

        sepet.addItem(pHirka, 2);
        sepet.print();

        DeliveryCostUtils delivery = new DeliveryCostUtils(12.0, 10.0, 2.99);
        delivery.calculateFor(sepet);

        /*
         * kazak x 2 = 140 tl -> -35 tl indirim -> 105 tl
         * hırka x 2 = 100 tl -> -20 tl indirim ->  80 tl
         *
         * toplam tutar   ->   240 tl
         * toplam indirim ->    55 tl
         * ödenecek tutar ->   185 tl
         * kargo ücreti   -> 34,99 tl
         *                  219,99 tl
         *
         */
        sepet.print();

        assert sepet.getCost().equals(219.99);
    }

}
