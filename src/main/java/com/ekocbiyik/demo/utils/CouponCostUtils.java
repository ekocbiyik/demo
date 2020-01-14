package com.ekocbiyik.demo.utils;

import com.ekocbiyik.demo.exceptions.DemoException;
import com.ekocbiyik.demo.exceptions.DemoExceptionCodeUtils;
import com.ekocbiyik.demo.model.Coupon;
import com.ekocbiyik.demo.model.DiscountType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ekocbiyik on 11.01.2020
 */
public class CouponCostUtils {

    public static Double calculateCouponDiscount(Set<Coupon> couponList, Double totalAmount) throws DemoException {

        /*
         * birden fazla kupon kullanılabilir,
         * kuponlar ilk olarak sağladığı indirim tutarına göre çoktan aza sıralanır.
         * fakat her kupon kullanıldıktan sonra kalan toplam fiyata göre diğer kuponlar kullandırılır
         *
         */

        Double totalDiscount = 0.0;

        for (Coupon coupon : getSortedCoupons(couponList, totalAmount)) {
            if (coupon.getDiscountType() == DiscountType.RATE && (totalAmount - totalDiscount) >= coupon.getMinPurchase()) {
                totalDiscount = validateCoupon((totalAmount * coupon.getDiscountAmount() / 100), totalAmount);
            }

            if (coupon.getDiscountType() == DiscountType.PRICE && ((totalAmount - totalDiscount) > coupon.getMinPurchase())) {
                totalDiscount = coupon.getDiscountAmount();
            }
        }
        return totalDiscount;
    }

    private static Double validateCoupon(Double totalDiscount, Double totalAmount) throws DemoException {
        if (totalDiscount > totalAmount) {
            throw new DemoException(DemoExceptionCodeUtils.INVALID_COUPON, "Invalid Coupon");
        }
        return totalDiscount;
    }

    private static List<Coupon> getSortedCoupons(Set<Coupon> couponList, Double totalAmount) {

        // her kuponun sğladığı indirim tutarı hesaplanır
        Map<Coupon, Double> calculatedCoupons = new HashMap<>();
        couponList.forEach(c -> calculatedCoupons.put(c, (c.getDiscountType() == DiscountType.RATE ? (totalAmount * c.getDiscountAmount() / 100) : c.getDiscountAmount())));

        // indirim tutarına göre sıralı liste oluşturulur (indirim tutarı küçükten büyüğe doğru)
        Set<Coupon> sortedCoupons = calculatedCoupons.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)).keySet();

        List<Coupon> finalCoupons = new ArrayList<>();
        finalCoupons.addAll(sortedCoupons);
        Collections.reverse(finalCoupons); // indirim tutarı büyükten küçüğe doğru sıralanır
        return finalCoupons;
    }

}
