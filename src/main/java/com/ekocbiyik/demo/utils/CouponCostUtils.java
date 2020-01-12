package com.ekocbiyik.demo.utils;

import com.ekocbiyik.demo.exceptions.DemoException;
import com.ekocbiyik.demo.exceptions.DemoExceptionCodeUtils;
import com.ekocbiyik.demo.model.Coupon;
import com.ekocbiyik.demo.model.DiscountType;

import java.util.Set;

/**
 * ekocbiyik on 11.01.2020
 */
public class CouponCostUtils {

    public static Double calculateCouponDiscount(Set<Coupon> couponList, Double totalAmount) throws DemoException {

        /*
         * birden fazla kupon kullanılabilir,
         * fakat her kupon kullanıldıktan sonra kalan toplam fiyata göre diğer kuponlar kullandırılır
         */

        Double totalDiscount = 0.0;

        for (Coupon coupon : couponList) {
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

}
