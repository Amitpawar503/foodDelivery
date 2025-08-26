package com.food.delivery.util;

import com.food.delivery.entity.Coupon;
import com.food.delivery.entity.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class PriceCalculationUtil {

    private PriceCalculationUtil() {
        // Utility class - prevent instantiation
    }

    public static BigDecimal calculateItemsTotal(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(item -> item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal calculateDiscount(BigDecimal itemsTotal, Coupon coupon) {
        if (coupon == null || !coupon.isValid()) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = itemsTotal
                .multiply(BigDecimal.valueOf(coupon.getDiscountPercent()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // Ensure discount doesn't exceed items total
        return discountAmount.compareTo(itemsTotal) > 0 ? itemsTotal : discountAmount;
    }

    public static BigDecimal calculateFinalTotal(BigDecimal itemsTotal, BigDecimal discount, BigDecimal tip) {
        BigDecimal tipAmount = tip != null ? tip : BigDecimal.ZERO;
        BigDecimal total = itemsTotal.subtract(discount).add(tipAmount);
        
        // Ensure total is not negative
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : total;
    }

    public static BigDecimal calculateOrderTotal(List<OrderItem> items, Coupon coupon, BigDecimal tip) {
        BigDecimal itemsTotal = calculateItemsTotal(items);
        BigDecimal discount = calculateDiscount(itemsTotal, coupon);
        return calculateFinalTotal(itemsTotal, discount, tip);
    }

    public static BigDecimal roundToTwoDecimals(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isValidPrice(BigDecimal price) {
        return price != null && 
               price.compareTo(BigDecimal.ZERO) >= 0 && 
               price.compareTo(BigDecimal.valueOf(10000)) <= 0;
    }

    public static boolean isValidTip(BigDecimal tip) {
        return tip == null || 
               (tip.compareTo(BigDecimal.ZERO) >= 0 && tip.compareTo(BigDecimal.valueOf(1000)) <= 0);
    }

    public static BigDecimal calculatePercentage(BigDecimal part, BigDecimal whole) {
        if (whole == null || whole.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100))
                   .divide(whole, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(taxRate)
                      .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateDeliveryFee(BigDecimal subtotal, BigDecimal baseFee, BigDecimal threshold) {
        if (subtotal == null || baseFee == null) {
            return BigDecimal.ZERO;
        }
        
        if (threshold != null && subtotal.compareTo(threshold) >= 0) {
            return BigDecimal.ZERO; // Free delivery above threshold
        }
        
        return baseFee;
    }
}
