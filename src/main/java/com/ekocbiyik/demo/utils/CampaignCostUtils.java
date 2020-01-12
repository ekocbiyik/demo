package com.ekocbiyik.demo.utils;

import com.ekocbiyik.demo.exceptions.DemoException;
import com.ekocbiyik.demo.exceptions.DemoExceptionCodeUtils;
import com.ekocbiyik.demo.model.Campaign;
import com.ekocbiyik.demo.model.Category;
import com.ekocbiyik.demo.model.DiscountType;
import com.ekocbiyik.demo.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ekocbiyik on 11.01.2020
 */
public class CampaignCostUtils {

    public static Map<Product, Integer> getCampaignProducts(Campaign campaign, Map<Product, Integer> productList) {

        /* kampanyanın, kapsadığı kategorideki ürünleri döner */

        Map<Product, Integer> products = new HashMap<>();
        productList.forEach((product, quantity) -> {
            if (isCategoryChilOfCampaignCategory(campaign, product.getCategory())) {
                products.put(product, quantity);
            }
        });
        return products;
    }

    public static Double calculateCampaignDiscount(Campaign campaign, Map<Product, Integer> products) throws DemoException {

        /* kampanyanın, kapsadığı kategorideki ürünlere sağladığı indirim tutarını döner
         * ürün sayısı, kampanyadaki sayı ile uyumlu olmalı
         */

        if ((campaign.getDiscountType() == DiscountType.RATE) && (products.entrySet().stream().mapToInt(Map.Entry::getValue).sum() >= campaign.getItemCount())) {
            Double totalAmount = 0.0;
            for (Map.Entry<Product, Integer> item : products.entrySet()) {
                totalAmount += item.getKey().getUnitPrize() * item.getValue();
            }
            return validateCampaign(totalAmount * campaign.getDiscountAmount() / 100, totalAmount);
        }

        if ((campaign.getDiscountType() == DiscountType.PRICE) && (products.entrySet().stream().mapToInt(Map.Entry::getValue).sum() >= campaign.getItemCount())) {
            return campaign.getDiscountAmount();
        }
        return 0.0;
    }

    public static List<Campaign> getApplicableCampaigns(Map<Campaign, Double> campaignList) {

        /* kampanyalar arasından categori bazında, indirim sağlayacak en uygun kampanyaları döner
         * kampanya categorisinin parentı başka bir kampanyanın parentı ile aynı mı?
         *      hayır ise   -> listeye ekle
         *      evet ise    -> kampanya diğer kampanyanın parentı mı
         *                          evet    ->  indirim tutarı büyükse eşitse listeye ekle, değilse devam
         *                          hayır   ->  aynı kategoride başka kampanya var mı?
         *                                          evet    ->  indirim tutarı büyük veya eşitse listeye ekle, değilse devam
         *                                          hayır   ->  listeye ekle
         */

        List<Campaign> applicableCampaigns = new ArrayList<>();

        for (Map.Entry<Campaign, Double> currentCampaign : campaignList.entrySet()) {

            // kampanya categorisinin parentı başka bir kampanyanın parentı ile aynı mı?
            Map<Campaign, Double> sameRootCategoryCampaigns = campaignList.entrySet().stream()
                    .filter(c -> (c.getKey() != currentCampaign.getKey()) && getRootCategory(c.getKey().getCategory()) == getRootCategory(currentCampaign.getKey().getCategory()))
                    .collect(Collectors.toMap(c -> c.getKey(), c -> c.getValue()));

            // eğer yoksa listeye eklenir
            if (sameRootCategoryCampaigns.size() == 0) {
                applicableCampaigns.add(currentCampaign.getKey());

            } else {

                // eğer varsa
                for (Map.Entry<Campaign, Double> sameCategoryCamp : sameRootCategoryCampaigns.entrySet()) {

                    // kampanya diğer kampanyanın parentı mı
                    if (isCategoryChilOfCampaignCategory(currentCampaign.getKey(), sameCategoryCamp.getKey().getCategory())) {

                        // evetse indirim tutarı büyükse listeye eklenir, değilse devam edilir
                        if (currentCampaign.getValue() >= sameCategoryCamp.getValue()) {
                            applicableCampaigns.add(currentCampaign.getKey());
                        } else {
                            applicableCampaigns.remove(currentCampaign);
                        }
                    } else {

                        // parent'ı ortak olan daha uygun başka kategory var
                        if (currentCampaign.getValue() < sameCategoryCamp.getValue()) {
                            continue;
                        }

                        // parentı değil ise aynı kategoride başka kampanya var mı?
                        Map<Campaign, Double> sameCategoryCampaigns = campaignList.entrySet().stream()
                                .filter(c -> (c.getKey() != currentCampaign.getKey()) && c.getKey().getCategory() == currentCampaign.getKey().getCategory())
                                .collect(Collectors.toMap(c -> c.getKey(), c -> c.getValue()));

                        // aynı kategoride başka kampanya varsa
                        if (sameCategoryCampaigns.size() != 0) {

                            int sameCategorySize = sameCategoryCampaigns.entrySet().stream()
                                    .filter(c -> currentCampaign.getValue() < c.getValue())
                                    .collect(Collectors.toList())
                                    .size();

                            //  indirim tutarı büyük veya eşitse listeye eklenir
                            if (sameCategorySize == 0) {
                                applicableCampaigns.add(currentCampaign.getKey());
                            }

                        } else {
                            // aynı kategoride başka kampanya yoksa
                            applicableCampaigns.add(currentCampaign.getKey());
                        }
                    }
                }
            }
        }

        return applicableCampaigns;
    }

    private static boolean isCategoryChilOfCampaignCategory(Campaign campaign, Category category) {
        /* kategori, kampanyanın kapsadığı bir kategoride mi? */
        return category == campaign.getCategory() ? true : category.getParent() != null ? isCategoryChilOfCampaignCategory(campaign, category.getParent()) : false;
    }

    private static Category getRootCategory(Category category) {
        return category.getParent() == null ? category : getRootCategory(category.getParent());
    }

    private static Double validateCampaign(Double totalDiscount, Double totalAmount) throws DemoException {
        if (totalDiscount > totalAmount) {
            throw new DemoException(DemoExceptionCodeUtils.INVALID_CAMPAIGN, "Invalid Campaign");
        }
        return totalDiscount;
    }


}
