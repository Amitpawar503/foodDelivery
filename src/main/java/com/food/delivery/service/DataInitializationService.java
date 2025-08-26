package com.food.delivery.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.delivery.entity.Coupon;
import com.food.delivery.entity.Meal;
import com.food.delivery.entity.Restaurant;
import com.food.delivery.entity.User;
import com.food.delivery.enums.UserRole;
import com.food.delivery.repository.CouponRepository;
import com.food.delivery.repository.MealRepository;
import com.food.delivery.repository.RestaurantRepository;
import com.food.delivery.repository.UserRepository;
import com.food.delivery.constants.ValidationConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@Service
@Slf4j
public class DataInitializationService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ObjectMapper objectMapper;

    private static final String SAMPLE_DATA_FILE = "data/sample-data.json";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeSampleData() {
        log.info("Starting sample data initialization from JSON file: {}", SAMPLE_DATA_FILE);

        try {
            // Check if any users already exist (indicating data has been initialized)
            if (userRepository.count() > 0) {
                log.info("Users already exist in database. Skipping sample data initialization.");
                return;
            }

            log.info("No users found in database. Proceeding with sample data initialization...");
            loadSampleDataFromJson();
            log.info("Sample data initialization completed successfully");
            
        } catch (IOException e) {
            log.error("Failed to load sample data from JSON file: {} - Error: {}", SAMPLE_DATA_FILE, e.getMessage(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred during sample data initialization: {}", e.getMessage(), e);
        }
    }


    private void loadSampleDataFromJson() throws IOException {
        ClassPathResource resource = new ClassPathResource(SAMPLE_DATA_FILE);
        
        if (!resource.exists()) {
            log.error("Sample data file not found: {}", SAMPLE_DATA_FILE);
            throw new IOException("Sample data file not found: " + SAMPLE_DATA_FILE);
        }

        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
        log.info("Successfully loaded JSON data from file: {}", SAMPLE_DATA_FILE);

        // Load data in dependency order
        loadUsersFromJson(rootNode.get("users"));
        loadRestaurantsFromJson(rootNode.get("restaurants"));
        loadMealsFromJson(rootNode.get("meals"));
        loadCouponsFromJson(rootNode.get("coupons"));
    }

    private void loadUsersFromJson(JsonNode usersNode) {
        if (usersNode == null || !usersNode.isArray()) {
            log.warn("No users data found in JSON or invalid format");
            return;
        }

        log.info("Loading {} users from JSON data", usersNode.size());
        int successCount = 0;
        int errorCount = 0;
        int skippedCount = 0;

        for (JsonNode userNode : usersNode) {
            try {
                String email = userNode.get("email").asText().trim();

                // Skip admin user creation as it's already created by Flyway migration
                if (ValidationConstants.ADMIN_EMAIL.equals(email)) {
                    log.info("Skipping admin user creation from JSON as it's already created by Flyway migration");
                    skippedCount++;
                    continue;
                }

                User user = createUserFromJson(userNode);
                userRepository.save(user);
                successCount++;
                log.debug("Created user: {} with role: {}", user.getEmail(), user.getRole());
            } catch (Exception e) {
                errorCount++;
                log.error("Failed to create user from JSON: {} - Error: {}", userNode, e.getMessage());
            }
        }

        log.info("User loading completed - Success: {}, Skipped: {}, Errors: {}", successCount, skippedCount, errorCount);
    }


    private User createUserFromJson(JsonNode userNode) {
        validateUserNode(userNode);
        
        User user = new User();
        user.setEmail(userNode.get("email").asText().trim());
        user.setPasswordHash(passwordEncoder.encode(userNode.get("password").asText()));
        user.setName(userNode.get("name").asText().trim());
        user.setRole(UserRole.valueOf(userNode.get("role").asText()));
        user.setBlocked(userNode.get("blocked").asBoolean());
        
        return user;
    }


    private void validateUserNode(JsonNode userNode) {
        if (!userNode.has("email") || !StringUtils.hasText(userNode.get("email").asText())) {
            throw new IllegalArgumentException("User email is required and cannot be empty");
        }
        if (!userNode.has("password") || !StringUtils.hasText(userNode.get("password").asText())) {
            throw new IllegalArgumentException("User password is required and cannot be empty");
        }
        if (!userNode.has("name") || !StringUtils.hasText(userNode.get("name").asText())) {
            throw new IllegalArgumentException("User name is required and cannot be empty");
        }
        if (!userNode.has("role")) {
            throw new IllegalArgumentException("User role is required");
        }
        if (!userNode.has("blocked")) {
            throw new IllegalArgumentException("User blocked status is required");
        }
    }


    private void loadRestaurantsFromJson(JsonNode restaurantsNode) {
        if (restaurantsNode == null || !restaurantsNode.isArray()) {
            log.warn("No restaurants data found in JSON or invalid format");
            return;
        }

        log.info("Loading {} restaurants from JSON data", restaurantsNode.size());
        int successCount = 0;
        int errorCount = 0;

        for (JsonNode restaurantNode : restaurantsNode) {
            try {
                Restaurant restaurant = createRestaurantFromJson(restaurantNode);
                restaurantRepository.save(restaurant);
                successCount++;
                log.debug("Created restaurant: {} owned by: {}", restaurant.getName(), restaurant.getOwner().getEmail());
            } catch (Exception e) {
                errorCount++;
                log.error("Failed to create restaurant from JSON: {} - Error: {}", restaurantNode, e.getMessage());
            }
        }

        log.info("Restaurant loading completed - Success: {}, Errors: {}", successCount, errorCount);
    }


    private Restaurant createRestaurantFromJson(JsonNode restaurantNode) {
        validateRestaurantNode(restaurantNode);
        
        String ownerEmail = restaurantNode.get("ownerEmail").asText().trim();
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with email: " + ownerEmail));

        Restaurant restaurant = new Restaurant();
        restaurant.setOwner(owner);
        restaurant.setName(restaurantNode.get("name").asText().trim());
        restaurant.setDescription(restaurantNode.get("description").asText().trim());
        restaurant.setBlocked(restaurantNode.get("blocked").asBoolean());
        
        return restaurant;
    }


    private void validateRestaurantNode(JsonNode restaurantNode) {
        if (!restaurantNode.has("name") || !StringUtils.hasText(restaurantNode.get("name").asText())) {
            throw new IllegalArgumentException("Restaurant name is required and cannot be empty");
        }
        if (!restaurantNode.has("description") || !StringUtils.hasText(restaurantNode.get("description").asText())) {
            throw new IllegalArgumentException("Restaurant description is required and cannot be empty");
        }
        if (!restaurantNode.has("ownerEmail") || !StringUtils.hasText(restaurantNode.get("ownerEmail").asText())) {
            throw new IllegalArgumentException("Restaurant owner email is required and cannot be empty");
        }
        if (!restaurantNode.has("blocked")) {
            throw new IllegalArgumentException("Restaurant blocked status is required");
        }
    }

    private void loadMealsFromJson(JsonNode mealsNode) {
        if (mealsNode == null || !mealsNode.isArray()) {
            log.warn("No meals data found in JSON or invalid format");
            return;
        }

        log.info("Loading {} meals from JSON data", mealsNode.size());
        int successCount = 0;
        int errorCount = 0;

        for (JsonNode mealNode : mealsNode) {
            try {
                Meal meal = createMealFromJson(mealNode);
                mealRepository.save(meal);
                successCount++;
                log.debug("Created meal: {} for restaurant: {}", meal.getName(), meal.getRestaurant().getName());
            } catch (Exception e) {
                errorCount++;
                log.error("Failed to create meal from JSON: {} - Error: {}", mealNode, e.getMessage());
            }
        }

        log.info("Meal loading completed - Success: {}, Errors: {}", successCount, errorCount);
    }

    private Meal createMealFromJson(JsonNode mealNode) {
        validateMealNode(mealNode);
        
        String restaurantName = mealNode.get("restaurantName").asText().trim();
        Restaurant restaurant = restaurantRepository.findByName(restaurantName)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with name: " + restaurantName));

        Meal meal = new Meal();
        meal.setRestaurant(restaurant);
        meal.setName(mealNode.get("name").asText().trim());
        meal.setDescription(mealNode.get("description").asText().trim());
        meal.setPrice(new BigDecimal(mealNode.get("price").asText()));
        
        return meal;
    }

    private void validateMealNode(JsonNode mealNode) {
        if (!mealNode.has("name") || !StringUtils.hasText(mealNode.get("name").asText())) {
            throw new IllegalArgumentException("Meal name is required and cannot be empty");
        }
        if (!mealNode.has("description") || !StringUtils.hasText(mealNode.get("description").asText())) {
            throw new IllegalArgumentException("Meal description is required and cannot be empty");
        }
        if (!mealNode.has("price")) {
            throw new IllegalArgumentException("Meal price is required");
        }
        if (!mealNode.has("restaurantName") || !StringUtils.hasText(mealNode.get("restaurantName").asText())) {
            throw new IllegalArgumentException("Meal restaurant name is required and cannot be empty");
        }
    }


    private void loadCouponsFromJson(JsonNode couponsNode) {
        if (couponsNode == null || !couponsNode.isArray()) {
            log.warn("No coupons data found in JSON or invalid format");
            return;
        }

        log.info("Loading {} coupons from JSON data", couponsNode.size());
        int successCount = 0;
        int errorCount = 0;

        for (JsonNode couponNode : couponsNode) {
            try {
                Coupon coupon = createCouponFromJson(couponNode);
                couponRepository.save(coupon);
                successCount++;
                log.debug("Created coupon: {} with {}% discount", coupon.getCode(), coupon.getDiscountPercent());
            } catch (Exception e) {
                errorCount++;
                log.error("Failed to create coupon from JSON: {} - Error: {}", couponNode, e.getMessage());
            }
        }

        log.info("Coupon loading completed - Success: {}, Errors: {}", successCount, errorCount);
    }

    private Coupon createCouponFromJson(JsonNode couponNode) {
        validateCouponNode(couponNode);
        
        Coupon coupon = new Coupon();
        coupon.setCode(couponNode.get("code").asText().trim());
        coupon.setDiscountPercent(couponNode.get("discountPercent").asInt());

        if (couponNode.has("expiresAt") && !couponNode.get("expiresAt").isNull()) {
            try {
                String expiresAtStr = couponNode.get("expiresAt").asText();
                LocalDateTime expiresAt = LocalDateTime.parse(expiresAtStr, DATE_TIME_FORMATTER);
                coupon.setExpiresAt(expiresAt);
            } catch (DateTimeParseException e) {
                log.warn("Invalid expiry date format for coupon: {} - Error: {}", coupon.getCode(), e.getMessage());
            }
        }

        coupon.setActive(couponNode.get("active").asBoolean());
        
        return coupon;
    }

    private void validateCouponNode(JsonNode couponNode) {
        if (!couponNode.has("code") || !StringUtils.hasText(couponNode.get("code").asText())) {
            throw new IllegalArgumentException("Coupon code is required and cannot be empty");
        }
        if (!couponNode.has("discountPercent")) {
            throw new IllegalArgumentException("Coupon discount percent is required");
        }
        if (!couponNode.has("active")) {
            throw new IllegalArgumentException("Coupon active status is required");
        }
    }
}
