package com.food.delivery.constants;

/**
 * Validation constants for the Food Delivery application.
 * Contains validation limits, patterns, and constraints.
 */
public final class ValidationConstants {

    private ValidationConstants() {
        // Constants class - prevent instantiation
    }

    // Length limits
    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_COUPON_CODE_LENGTH = 50;
    public static final int MAX_ORDER_ITEMS = 20;
    public static final int MAX_QUANTITY = 100;
    public static final int MAX_TIP_AMOUNT = 1000;
    public static final int MAX_PRICE = 10000;

    // Discount limits
    public static final int MIN_DISCOUNT_PERCENT = 1;
    public static final int MAX_DISCOUNT_PERCENT = 100;

    // Price limits
    public static final double MIN_PRICE = 0.0;
    public static final double MAX_PRICE_DOUBLE = 10000.0;

    // Quantity limits
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY_VALUE = 100;

    // Tip limits
    public static final double MIN_TIP_AMOUNT = 0.0;
    public static final double MAX_TIP_AMOUNT_DOUBLE = 1000.0;

    // Pagination limits
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE = 0;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Email pattern
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Password pattern (at least 6 characters, can contain letters, numbers, and special characters)
    public static final String PASSWORD_PATTERN = "^.{6,100}$";

    // Name pattern (letters, spaces, hyphens, apostrophes)
    public static final String NAME_PATTERN = "^[a-zA-Z\\s\\-']+$";

    // Coupon code pattern (alphanumeric and hyphens)
    public static final String COUPON_CODE_PATTERN = "^[a-zA-Z0-9\\-]+$";

    // Price pattern (positive decimal with up to 2 decimal places)
    public static final String PRICE_PATTERN = "^\\d+(\\.\\d{1,2})?$";

    // UUID pattern
    public static final String UUID_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    // Date time pattern (ISO format)
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    // Time constants
    public static final long JWT_EXPIRATION_MS = 86400000L; // 24 hours
    public static final long COUPON_EXPIRY_WARNING_HOURS = 24L; // 1 day

    // Security constants
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String ADMIN_EMAIL = "admin@fooddelivery.com";

    // Database constants
    public static final String UUID_COLUMN_DEFINITION = "VARCHAR(36)";
    public static final String TEXT_COLUMN_DEFINITION = "TEXT";
    public static final String DECIMAL_PRECISION_SCALE = "10,2";

    // Cache constants
    public static final String USER_CACHE = "users";
    public static final String RESTAURANT_CACHE = "restaurants";
    public static final String MEAL_CACHE = "meals";
    public static final String ORDER_CACHE = "orders";
    public static final String COUPON_CACHE = "coupons";
}
