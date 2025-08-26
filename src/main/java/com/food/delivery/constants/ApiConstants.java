package com.food.delivery.constants;

/**
 * API constants for the Food Delivery application.
 * Contains API paths, endpoints, and related constants.
 */
public final class ApiConstants {

    private ApiConstants() {
        // Constants class - prevent instantiation
    }

    // Base API path
    public static final String API_BASE_PATH = "/api";

    // Authentication paths
    public static final String AUTH_PATH = "/auth";
    public static final String AUTH_CREATE_PATH = "/create";
    public static final String AUTH_GET_PATH = "/get";

    // Admin paths
    public static final String ADMIN_PATH = "/admin";
    public static final String ADMIN_USERS_PATH = "/users";
    public static final String ADMIN_RESTAURANTS_PATH = "/restaurants";
    public static final String ADMIN_COUPONS_PATH = "/coupons";

    // Restaurant paths
    public static final String RESTAURANTS_PATH = "/restaurants";
    public static final String RESTAURANTS_CREATE_PATH = "/create";
    public static final String RESTAURANTS_GET_PATH = "/get";
    public static final String RESTAURANTS_EDIT_PATH = "/edit";
    public static final String RESTAURANTS_DELETE_PATH = "/delete";
    public static final String RESTAURANTS_SEARCH_PATH = "/search";
    public static final String RESTAURANTS_OWNER_PATH = "/owner";

    // Meal paths
    public static final String MEALS_PATH = "/meals";
    public static final String MEALS_CREATE_PATH = "/create";
    public static final String MEALS_GET_PATH = "/get";
    public static final String MEALS_EDIT_PATH = "/edit";
    public static final String MEALS_DELETE_PATH = "/delete";
    public static final String MEALS_SEARCH_PATH = "/search";
    public static final String MEALS_PRICE_RANGE_PATH = "/price-range";
    public static final String MEALS_RESTAURANT_PATH = "/restaurant";

    // Order paths
    public static final String ORDERS_PATH = "/orders";
    public static final String ORDERS_CREATE_PATH = "/create";
    public static final String ORDERS_GET_PATH = "/get";
    public static final String ORDERS_EDIT_PATH = "/edit";
    public static final String ORDERS_DELETE_PATH = "/delete";
    public static final String ORDERS_STATUS_PATH = "/status";

    // Coupon paths
    public static final String COUPONS_PATH = "/coupons";
    public static final String COUPONS_CREATE_PATH = "/create";
    public static final String COUPONS_GET_PATH = "/get";
    public static final String COUPONS_EDIT_PATH = "/edit";
    public static final String COUPONS_DELETE_PATH = "/delete";

    // User paths
    public static final String USERS_PATH = "/users";
    public static final String USERS_CREATE_PATH = "/create";
    public static final String USERS_GET_PATH = "/get";
    public static final String USERS_EDIT_PATH = "/edit";
    public static final String USERS_DELETE_PATH = "/delete";
    public static final String USERS_BLOCK_PATH = "/block";
    public static final String USERS_UNBLOCK_PATH = "/unblock";

    // Owner block paths
    public static final String OWNER_BLOCKS_PATH = "/owner/blocks";
    public static final String OWNER_BLOCKS_CREATE_PATH = "/create";
    public static final String OWNER_BLOCKS_DELETE_PATH = "/delete";

    // Path variables
    public static final String ID_PATH_VARIABLE = "/{id}";
    public static final String RESTAURANT_ID_PATH_VARIABLE = "/{restaurantId}";
    public static final String USER_ID_PATH_VARIABLE = "/{userId}";

    // Query parameters
    public static final String PAGE_PARAM = "page";
    public static final String SIZE_PARAM = "size";
    public static final String SORT_PARAM = "sort";
    public static final String DIRECTION_PARAM = "direction";
    public static final String SEARCH_PARAM = "q";
    public static final String STATUS_PARAM = "status";
    public static final String FROM_DATE_PARAM = "from";
    public static final String TO_DATE_PARAM = "to";
    public static final String MIN_PRICE_PARAM = "minPrice";
    public static final String MAX_PRICE_PARAM = "maxPrice";
    public static final String ROLE_PARAM = "role";

    // HTTP methods
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";
    public static final String PATCH_METHOD = "PATCH";

    // Content types
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/xml";
    public static final String TEXT_PLAIN = "text/plain";

    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String USER_AGENT_HEADER = "User-Agent";

    // Swagger/OpenAPI
    public static final String SWAGGER_UI_PATH = "/swagger-ui.html";
    public static final String API_DOCS_PATH = "/v3/api-docs";
    public static final String API_DOCS_YAML_PATH = "/v3/api-docs.yaml";

    // Complete API endpoints
    public static final String AUTH_CREATE_ENDPOINT = API_BASE_PATH + AUTH_PATH + AUTH_CREATE_PATH;
    public static final String AUTH_GET_ENDPOINT = API_BASE_PATH + AUTH_PATH + AUTH_GET_PATH;
    
    public static final String RESTAURANTS_CREATE_ENDPOINT = API_BASE_PATH + RESTAURANTS_PATH + RESTAURANTS_CREATE_PATH;
    public static final String RESTAURANTS_GET_ENDPOINT = API_BASE_PATH + RESTAURANTS_PATH + RESTAURANTS_GET_PATH;
    public static final String RESTAURANTS_GET_BY_ID_ENDPOINT = API_BASE_PATH + RESTAURANTS_PATH + RESTAURANTS_GET_PATH + ID_PATH_VARIABLE;
    public static final String RESTAURANTS_EDIT_ENDPOINT = API_BASE_PATH + RESTAURANTS_PATH + RESTAURANTS_EDIT_PATH + ID_PATH_VARIABLE;
    public static final String RESTAURANTS_DELETE_ENDPOINT = API_BASE_PATH + RESTAURANTS_PATH + RESTAURANTS_DELETE_PATH + ID_PATH_VARIABLE;
    public static final String RESTAURANTS_SEARCH_ENDPOINT = API_BASE_PATH + RESTAURANTS_PATH + RESTAURANTS_SEARCH_PATH;
    public static final String RESTAURANTS_OWNER_ENDPOINT = API_BASE_PATH + RESTAURANTS_PATH + RESTAURANTS_OWNER_PATH;

    public static final String MEALS_CREATE_ENDPOINT = API_BASE_PATH + MEALS_PATH + MEALS_CREATE_PATH + MEALS_RESTAURANT_PATH + RESTAURANT_ID_PATH_VARIABLE;
    public static final String MEALS_GET_BY_ID_ENDPOINT = API_BASE_PATH + MEALS_PATH + MEALS_GET_PATH + ID_PATH_VARIABLE;
    public static final String MEALS_GET_BY_RESTAURANT_ENDPOINT = API_BASE_PATH + MEALS_PATH + MEALS_GET_PATH + MEALS_RESTAURANT_PATH + RESTAURANT_ID_PATH_VARIABLE;
    public static final String MEALS_SEARCH_ENDPOINT = API_BASE_PATH + MEALS_PATH + MEALS_SEARCH_PATH + MEALS_RESTAURANT_PATH + RESTAURANT_ID_PATH_VARIABLE;
    public static final String MEALS_PRICE_RANGE_ENDPOINT = API_BASE_PATH + MEALS_PATH + MEALS_PRICE_RANGE_PATH + MEALS_RESTAURANT_PATH + RESTAURANT_ID_PATH_VARIABLE;
    public static final String MEALS_EDIT_ENDPOINT = API_BASE_PATH + MEALS_PATH + MEALS_EDIT_PATH + ID_PATH_VARIABLE;
    public static final String MEALS_DELETE_ENDPOINT = API_BASE_PATH + MEALS_PATH + MEALS_DELETE_PATH + ID_PATH_VARIABLE;

    public static final String ORDERS_CREATE_ENDPOINT = API_BASE_PATH + ORDERS_PATH + ORDERS_CREATE_PATH;
    public static final String ORDERS_GET_ENDPOINT = API_BASE_PATH + ORDERS_PATH + ORDERS_GET_PATH;
    public static final String ORDERS_GET_BY_ID_ENDPOINT = API_BASE_PATH + ORDERS_PATH + ORDERS_GET_PATH + ID_PATH_VARIABLE;
    public static final String ORDERS_EDIT_ENDPOINT = API_BASE_PATH + ORDERS_PATH + ORDERS_EDIT_PATH + ID_PATH_VARIABLE;
    public static final String ORDERS_EDIT_STATUS_ENDPOINT = API_BASE_PATH + ORDERS_PATH + ORDERS_EDIT_PATH + ORDERS_STATUS_PATH + ID_PATH_VARIABLE;
    public static final String ORDERS_DELETE_ENDPOINT = API_BASE_PATH + ORDERS_PATH + ORDERS_DELETE_PATH + ID_PATH_VARIABLE;

    public static final String ADMIN_USERS_GET_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_USERS_PATH + USERS_GET_PATH;
    public static final String ADMIN_USERS_GET_BY_ID_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_USERS_PATH + USERS_GET_PATH + ID_PATH_VARIABLE;
    public static final String ADMIN_USERS_EDIT_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_USERS_PATH + USERS_EDIT_PATH + ID_PATH_VARIABLE;
    public static final String ADMIN_USERS_DELETE_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_USERS_PATH + USERS_DELETE_PATH + ID_PATH_VARIABLE;
    public static final String ADMIN_USERS_BLOCK_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_USERS_PATH + USERS_CREATE_PATH + USERS_BLOCK_PATH + ID_PATH_VARIABLE;
    public static final String ADMIN_USERS_UNBLOCK_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_USERS_PATH + USERS_CREATE_PATH + USERS_UNBLOCK_PATH + ID_PATH_VARIABLE;

    public static final String ADMIN_RESTAURANTS_GET_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_RESTAURANTS_PATH + RESTAURANTS_GET_PATH;
    public static final String ADMIN_RESTAURANTS_BLOCK_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_RESTAURANTS_PATH + RESTAURANTS_CREATE_PATH + USERS_BLOCK_PATH + ID_PATH_VARIABLE;
    public static final String ADMIN_RESTAURANTS_UNBLOCK_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_RESTAURANTS_PATH + RESTAURANTS_CREATE_PATH + USERS_UNBLOCK_PATH + ID_PATH_VARIABLE;

    public static final String ADMIN_COUPONS_CREATE_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_COUPONS_PATH + COUPONS_CREATE_PATH;
    public static final String ADMIN_COUPONS_GET_ENDPOINT = API_BASE_PATH + ADMIN_PATH + ADMIN_COUPONS_PATH + COUPONS_GET_PATH;

    public static final String OWNER_BLOCKS_CREATE_ENDPOINT = API_BASE_PATH + OWNER_BLOCKS_PATH + OWNER_BLOCKS_CREATE_PATH + MEALS_RESTAURANT_PATH + RESTAURANT_ID_PATH_VARIABLE + USER_ID_PATH_VARIABLE;
    public static final String OWNER_BLOCKS_DELETE_ENDPOINT = API_BASE_PATH + OWNER_BLOCKS_PATH + OWNER_BLOCKS_DELETE_PATH + MEALS_RESTAURANT_PATH + RESTAURANT_ID_PATH_VARIABLE + USER_ID_PATH_VARIABLE;
}
