package com.food.delivery.constants;

/**
 * Error constants for the Food Delivery application.
 * Contains error codes, response codes, and error descriptions.
 */
public final class ErrorConstants {

    private ErrorConstants() {
        // Constants class - prevent instantiation
    }

    // HTTP Status Codes
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int INTERNAL_SERVER_ERROR = 500;

    // Error Codes
    public static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    public static final String AUTHENTICATION_ERROR_CODE = "AUTHENTICATION_ERROR";
    public static final String AUTHORIZATION_ERROR_CODE = "AUTHORIZATION_ERROR";
    public static final String RESOURCE_NOT_FOUND_CODE = "RESOURCE_NOT_FOUND";
    public static final String CONFLICT_ERROR_CODE = "CONFLICT_ERROR";
    public static final String BUSINESS_LOGIC_ERROR_CODE = "BUSINESS_LOGIC_ERROR";
    public static final String SYSTEM_ERROR_CODE = "SYSTEM_ERROR";

    // Common Error Messages
    public static final String VALIDATION_ERROR_MESSAGE = "Validation error occurred";
    public static final String AUTHENTICATION_ERROR_MESSAGE = "Authentication failed";
    public static final String AUTHORIZATION_ERROR_MESSAGE = "Access denied";
    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found";
    public static final String CONFLICT_ERROR_MESSAGE = "Resource conflict";
    public static final String BUSINESS_LOGIC_ERROR_MESSAGE = "Business logic error";
    public static final String SYSTEM_ERROR_MESSAGE = "Internal server error";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error occurred";

    // User-related Error Messages
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists";
    public static final String USER_BLOCKED_MESSAGE = "User is blocked";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials";
    public static final String INSUFFICIENT_PERMISSIONS_MESSAGE = "Insufficient permissions";
    public static final String USER_EMAIL_EXISTS_MESSAGE = "User with this email already exists";

    // Restaurant-related Error Messages
    public static final String RESTAURANT_NOT_FOUND_MESSAGE = "Restaurant not found";
    public static final String RESTAURANT_ALREADY_EXISTS_MESSAGE = "Restaurant already exists";
    public static final String RESTAURANT_BLOCKED_MESSAGE = "Restaurant is blocked";
    public static final String RESTAURANT_OWNER_MISMATCH_MESSAGE = "Restaurant owner mismatch";
    public static final String RESTAURANT_NAME_EXISTS_MESSAGE = "Restaurant with this name already exists";

    // Meal-related Error Messages
    public static final String MEAL_NOT_FOUND_MESSAGE = "Meal not found";
    public static final String MEAL_ALREADY_EXISTS_MESSAGE = "Meal already exists";
    public static final String MEAL_NAME_EXISTS_MESSAGE = "Meal with this name already exists in the restaurant";
    public static final String INVALID_MEAL_PRICE_MESSAGE = "Invalid meal price";

    // Order-related Error Messages
    public static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";
    public static final String ORDER_ALREADY_CANCELLED_MESSAGE = "Order is already cancelled";
    public static final String ORDER_ALREADY_COMPLETED_MESSAGE = "Order is already completed";
    public static final String INVALID_ORDER_STATUS_TRANSITION_MESSAGE = "Invalid order status transition";
    public static final String ORDER_ACCESS_DENIED_MESSAGE = "Access denied to this order";
    public static final String EMPTY_ORDER_ITEMS_MESSAGE = "Order must contain at least one item";
    public static final String INVALID_ORDER_QUANTITY_MESSAGE = "Invalid order quantity";

    // Coupon-related Error Messages
    public static final String COUPON_NOT_FOUND_MESSAGE = "Coupon not found";
    public static final String COUPON_ALREADY_EXISTS_MESSAGE = "Coupon already exists";
    public static final String COUPON_EXPIRED_MESSAGE = "Coupon has expired";
    public static final String COUPON_INACTIVE_MESSAGE = "Coupon is inactive";
    public static final String COUPON_CODE_EXISTS_MESSAGE = "Coupon with this code already exists";
    public static final String INVALID_COUPON_DISCOUNT_MESSAGE = "Invalid coupon discount percentage";

    // Block-related Error Messages
    public static final String USER_ALREADY_BLOCKED_MESSAGE = "User is already blocked";
    public static final String USER_NOT_BLOCKED_MESSAGE = "User is not blocked";
    public static final String BLOCK_NOT_FOUND_MESSAGE = "Block record not found";

    // Validation Error Messages
    public static final String INVALID_EMAIL_FORMAT_MESSAGE = "Invalid email format";
    public static final String INVALID_PASSWORD_LENGTH_MESSAGE = "Password must be between 6 and 100 characters";
    public static final String INVALID_NAME_LENGTH_MESSAGE = "Name must not exceed 255 characters";
    public static final String INVALID_DESCRIPTION_LENGTH_MESSAGE = "Description must not exceed 1000 characters";
    public static final String INVALID_PRICE_RANGE_MESSAGE = "Price must be between 0 and 10000";
    public static final String INVALID_DISCOUNT_RANGE_MESSAGE = "Discount must be between 1 and 100 percent";
    public static final String INVALID_TIP_AMOUNT_MESSAGE = "Tip amount must be between 0 and 1000";
    public static final String INVALID_QUANTITY_MESSAGE = "Quantity must be between 1 and 100";
    public static final String INVALID_PAGE_PARAMETERS_MESSAGE = "Invalid page parameters";

    // Success Messages
    public static final String USER_CREATED_SUCCESS_MESSAGE = "User created successfully";
    public static final String USER_UPDATED_SUCCESS_MESSAGE = "User updated successfully";
    public static final String USER_DELETED_SUCCESS_MESSAGE = "User deleted successfully";
    public static final String USER_BLOCKED_SUCCESS_MESSAGE = "User blocked successfully";
    public static final String USER_UNBLOCKED_SUCCESS_MESSAGE = "User unblocked successfully";

    public static final String RESTAURANT_CREATED_SUCCESS_MESSAGE = "Restaurant created successfully";
    public static final String RESTAURANT_UPDATED_SUCCESS_MESSAGE = "Restaurant updated successfully";
    public static final String RESTAURANT_DELETED_SUCCESS_MESSAGE = "Restaurant deleted successfully";
    public static final String RESTAURANT_BLOCKED_SUCCESS_MESSAGE = "Restaurant blocked successfully";
    public static final String RESTAURANT_UNBLOCKED_SUCCESS_MESSAGE = "Restaurant unblocked successfully";

    public static final String MEAL_CREATED_SUCCESS_MESSAGE = "Meal created successfully";
    public static final String MEAL_UPDATED_SUCCESS_MESSAGE = "Meal updated successfully";
    public static final String MEAL_DELETED_SUCCESS_MESSAGE = "Meal deleted successfully";

    public static final String ORDER_PLACED_SUCCESS_MESSAGE = "Order placed successfully";
    public static final String ORDER_UPDATED_SUCCESS_MESSAGE = "Order updated successfully";
    public static final String ORDER_CANCELLED_SUCCESS_MESSAGE = "Order cancelled successfully";
    public static final String ORDER_STATUS_UPDATED_SUCCESS_MESSAGE = "Order status updated successfully";

    public static final String COUPON_CREATED_SUCCESS_MESSAGE = "Coupon created successfully";
    public static final String COUPON_UPDATED_SUCCESS_MESSAGE = "Coupon updated successfully";
    public static final String COUPON_DELETED_SUCCESS_MESSAGE = "Coupon deleted successfully";

    public static final String USER_BLOCKED_FOR_RESTAURANT_SUCCESS_MESSAGE = "User blocked for restaurant successfully";
    public static final String USER_UNBLOCKED_FOR_RESTAURANT_SUCCESS_MESSAGE = "User unblocked for restaurant successfully";

    public static final String LOGIN_SUCCESS_MESSAGE = "Login successful";
    public static final String REGISTRATION_SUCCESS_MESSAGE = "Registration successful";
}
