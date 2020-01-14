package com.ekocbiyik.demo.controller;

import com.ekocbiyik.demo.exceptions.DemoException;
import com.ekocbiyik.demo.exceptions.DemoExceptionCodeUtils;
import com.ekocbiyik.demo.model.Campaign;
import com.ekocbiyik.demo.model.Coupon;
import com.ekocbiyik.demo.model.Product;
import com.ekocbiyik.demo.thirdpart.wagu.Block;
import com.ekocbiyik.demo.thirdpart.wagu.Board;
import com.ekocbiyik.demo.thirdpart.wagu.Table;
import com.ekocbiyik.demo.utils.CampaignCostUtils;
import com.ekocbiyik.demo.utils.CouponCostUtils;
import com.ekocbiyik.demo.utils.DeliveryCostUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * ekocbiyik on 11.01.2020
 */
public class ShoppingCart implements IShoppingCartController {

    private final Logger logger = LoggerFactory.getLogger(ShoppingCart.class);

    private Map<Product, Integer> productList = new HashMap<>();    // ürün & miktarı
    private Map<Campaign, Double> campaignList = new HashMap<>();   // kampanya & indirim tutarı
    private Map<Campaign, Double> applicableCampaignList = new HashMap<>();   // kampanyalar arasında max indirim tutarını sağlayanlar
    private Set<Coupon> couponList = new HashSet<>();
    private Double totalAmount = 0.0;
    private Double couponDiscount = 0.0;
    private Double campaignDiscount = 0.0;
    private Double deliveryCost = 0.0;

    @Override
    public void addItem(Product product, Integer quantity) throws DemoException {

        if (product == null || quantity == null) {
            throw new DemoException(DemoExceptionCodeUtils.NULL_PARAMETER, "Null parameter Exception");
        }

        /*
         * ürün varsa mikatırı güncellenir, yoksa miktarı ile eklenir
         */
        Integer productCount = productList.get(product);
        productList.put(product, (productCount == null ? 0 : productCount) + quantity);

        logger.info("Cart updated with {} x {}!", product.getProductTitle(), quantity);
        refreshAmountCalculation();
    }

    @Override
    public void applyDiscount(Campaign... newCampaigns) throws DemoException {

        if (newCampaigns == null || newCampaigns.length == 0) {
            throw new DemoException(DemoExceptionCodeUtils.EMPTY_CAMPAIGN, "Empty campaign parameter Exception");
        }

        /*
         * kampanya, sağladığı indirim miktarıyla birlikte listeye eklenir. Başlangıç tutarı 0'dır
         */
        Arrays.asList(newCampaigns).forEach(newCmp -> campaignList.put(newCmp, 0.0));
        logger.info("Campaigns updated!");
        refreshAmountCalculation();
    }

    @Override
    public void applyCoupon(Coupon coupon) throws DemoException {
        if (coupon == null) {
            throw new DemoException(DemoExceptionCodeUtils.EMPTY_COUPON, "Empty coupon parameter Exception");
        }
        couponList.add(coupon);
        logger.info("Coupons updated!");
        refreshAmountCalculation();
    }

    @Override
    public Double getTotalAmountAfterDiscounts() {
        return totalAmount - (getCampaignDiscount() + getCouponDiscount());
    }

    @Override
    public Double getCouponDiscount() {
        return couponDiscount;
    }

    @Override
    public Double getCampaignDiscount() {
        return campaignDiscount;
    }

    @Override
    public Double getDeliveryCost() {
        return deliveryCost;
    }

    @Override
    public Double getCost() {
        return getTotalAmountAfterDiscounts() + getDeliveryCost();
    }

    @Override
    public void print() {

        List<String> headersList = Arrays.asList("CATEGORY NAME", "PRODUCT NAME", "QUANTITY", "UNIT PRIZE", "TOTAL PRIZE");
        List<List<String>> rowsList = new ArrayList<>();

        productList.forEach((product, quantity) -> rowsList.add(Arrays.asList(
                String.valueOf(product.getCategory().getCategoryTitle()),
                String.valueOf(product.getProductTitle()),
                String.valueOf(quantity),
                String.valueOf(product.getUnitPrize()),
                String.valueOf(product.getUnitPrize() * quantity)
        )));

        rowsList.add(Arrays.asList(" ", " ", " ", " ", " "));
        rowsList.add(Arrays.asList(" ", " ", " ", " ", " "));
        rowsList.add(Arrays.asList(" ", " ", " ", "TOTAL", String.valueOf(totalAmount)));
        rowsList.add(Arrays.asList(" ", " ", " ", "DISCOUNT", "-" + (getCouponDiscount() + getCampaignDiscount())));
        rowsList.add(Arrays.asList(" ", " ", " ", "AFTER DISCOUNT", String.valueOf(getTotalAmountAfterDiscounts())));
        rowsList.add(Arrays.asList(" ", " ", " ", "DELIVERY COST", String.valueOf(getDeliveryCost())));
        rowsList.add(Arrays.asList(" ", " ", " ", "COST", String.valueOf(getCost())));

        Board board = new Board(140);
        Table table = new Table(board, 140, headersList, rowsList);
        Block tableBlock = table.tableToBlocks();
        board.setInitialBlock(tableBlock);
        board.build();
        table.setGridMode(Table.GRID_FULL).setColWidthsList(Arrays.asList(30, 30, 10, 10, 10));
        System.out.println(board.getPreview());
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    private void refreshAmountCalculation() throws DemoException {

        /*
         * her işlem adımında (kampanya ekle-çıkar, ürün ekle-çıkar vb.) tutarlar yeniden hesaplanır.
         *      her kampanyanın kategorisine ait ürünler bulunur
         *      kategoriye göre sağladığı indirim tutarı hesaplanır
         *      indirim tutarı kampanya listesinde güncellenir
         */

        for (Map.Entry<Campaign, Double> campItem : campaignList.entrySet()) {
            Map<Product, Integer> products = CampaignCostUtils.getCampaignProducts(campItem.getKey(), productList);
            campaignList.put(campItem.getKey(), CampaignCostUtils.calculateCampaignDiscountPerProduct(campItem.getKey(), products));
        }

        // kampanyalar arasından en uygun olanları hesaplanır
        CampaignCostUtils.getApplicableCampaigns(campaignList).forEach(c -> applicableCampaignList.put(c, campaignList.get(c)));

        totalAmount = 0.0;
        productList.forEach((product, quantity) -> totalAmount += product.getUnitPrize() * quantity);   // toplam fiyat
        campaignDiscount = CampaignCostUtils.calculateCampaingDiscounts(applicableCampaignList);        // kampanya indirimleri
        couponDiscount = CouponCostUtils.calculateCouponDiscount(couponList, (totalAmount - campaignDiscount)); // kupon indirimleri
    }

    public int getNumberOfProducts() {
        return productList.size();  // herbir farklı ürün miktarı
    }

    public int getNumberOfDeliveries() {
        return DeliveryCostUtils.getNumberOfDeliveries(productList.keySet()); // farklı kategorideki ürünler
    }

}
