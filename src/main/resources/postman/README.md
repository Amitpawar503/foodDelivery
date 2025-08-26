# Food Delivery API - Postman Collections

This directory contains separate Postman collections for each controller in the Food Delivery API. Each collection is focused on a specific domain and includes all relevant endpoints with different parameters and scenarios.

## 📁 Collections Overview

### 1. **AuthController.postman_collection.json**
**Authentication endpoints**
- Register new users (Customer/Owner) - `POST /api/auth/create`
- Login for all user types (Admin/Customer/Owner) - `POST /api/auth/get`
- JWT token generation

### 2. **RestaurantController.postman_collection.json**
**Restaurant management endpoints**
- Get all restaurants (with pagination) - `GET /api/restaurants/get`
- Search restaurants - `GET /api/restaurants/search`
- Get restaurant by ID - `GET /api/restaurants/get/{id}`
- Get owner's restaurants - `GET /api/restaurants/owner`
- Create restaurant - `POST /api/restaurants/create` (Owner only)
- Update restaurant - `PUT /api/restaurants/edit/{id}` (Owner only)
- Delete restaurant - `DELETE /api/restaurants/delete/{id}` (Owner only)

### 3. **MealController.postman_collection.json**
**Meal management endpoints**
- Get meals by restaurant (with pagination) - `GET /api/meals/get/restaurant/{restaurantId}`
- Search meals by restaurant - `GET /api/meals/search/restaurant/{restaurantId}`
- Get meals by price range - `GET /api/meals/price-range/restaurant/{restaurantId}`
- Get meal by ID - `GET /api/meals/get/{id}`
- Create meal - `POST /api/meals/create/restaurant/{restaurantId}` (Owner only)
- Update meal - `PUT /api/meals/edit/{id}` (Owner only)
- Delete meal - `DELETE /api/meals/delete/{id}` (Owner only)

### 4. **OrderController.postman_collection.json**
**Order management endpoints**
- Get user's orders (with pagination, status filtering, date filtering) - `GET /api/orders/get`
- Get order by ID - `GET /api/orders/get/{id}`
- Place orders (with/without tip and coupon) - `POST /api/orders/create` (Customer only)
- Update order status (role-based permissions) - `PUT /api/orders/edit/status/{id}`
- Update order details - `PUT /api/orders/edit/{id}` (Customer/Admin only)
- Cancel orders - `DELETE /api/orders/delete/{id}`

### 5. **OwnerBlockController.postman_collection.json**
**Owner block management endpoints**
- Block user from restaurant (Owner only) - `POST /api/owner/blocks/create/restaurant/{restaurantId}/user/{userId}`
- Unblock user from restaurant (Owner only) - `DELETE /api/owner/blocks/delete/restaurant/{restaurantId}/user/{userId}`

### 6. **AdminUserController.postman_collection.json**
**Admin user management endpoints**
- Get all users (with pagination, role filtering) - `GET /api/admin/users/get`
- Get user by ID - `GET /api/admin/users/get/{id}`
- Update user details - `PUT /api/admin/users/edit/{id}`
- Delete user - `DELETE /api/admin/users/delete/{id}`
- Block user - `POST /api/admin/users/create/block/{id}`
- Unblock user - `POST /api/admin/users/create/unblock/{id}`

### 7. **AdminRestaurantController.postman_collection.json**
**Admin restaurant management endpoints**
- Get all restaurants (with pagination, sorting) - `GET /api/admin/restaurants/get`
- Block restaurant - `POST /api/admin/restaurants/create/block/{id}`
- Unblock restaurant - `POST /api/admin/restaurants/create/unblock/{id}`

### 8. **AdminCouponController.postman_collection.json**
**Admin coupon management endpoints**
- Create new coupons - `POST /api/admin/coupons/create`
- Get all coupons (with pagination) - `GET /api/admin/coupons/get`

## 🚀 How to Use

### 1. Import Collections
1. Open Postman
2. Click "Import" button
3. Select the collection files you want to import
4. Each collection will be imported separately

### 2. Set Up Environment Variables
Create a Postman environment with the following variables:

```json
{
  "baseUrl": "http://localhost:8080",
  "adminToken": "",
  "customerToken": "",
  "ownerToken": "",
  "restaurantId": "",
  "mealId": "",
  "orderId": "",
  "userId": ""
}
```

### 3. Authentication Flow
1. **First, authenticate to get tokens:**
   - Use `AuthController` collection
   - Run "Login Admin" to get admin token
   - Run "Login Customer" to get customer token
   - Run "Login Owner" to get owner token
   - Copy the JWT tokens from responses to environment variables

2. **Then use other collections:**
   - Set the appropriate token in environment variables
   - Use the collections based on your role and needs

### 4. Testing Workflow

#### For Customers:
1. Import: `AuthController`, `RestaurantController`, `MealController`, `OrderController`
2. Login as customer
3. Browse restaurants and meals
4. Place orders
5. Track order status

#### For Restaurant Owners:
1. Import: `AuthController`, `RestaurantController`, `MealController`, `OrderController`, `OwnerBlockController`
2. Login as owner
3. Manage restaurants and meals
4. Process orders
5. Block/unblock users

#### For Administrators:
1. Import all collections
2. Login as admin
3. Manage users, restaurants, and coupons
4. Monitor system-wide operations

## 🔧 Collection Features

### Variables
Each collection includes relevant variables:
- `baseUrl`: API base URL
- Role-specific tokens: `adminToken`, `customerToken`, `ownerToken`
- Entity IDs: `restaurantId`, `mealId`, `orderId`, `userId`

### Request Examples
Each collection includes multiple request examples:
- Basic requests
- Requests with pagination
- Requests with filtering
- Requests with different parameters

### Authorization
- Public endpoints: No authorization required
- Protected endpoints: Require appropriate JWT token
- Role-based access: Different tokens for different roles

## 📋 Testing Scenarios

### Authentication Testing
- Register new users
- Login with valid credentials
- Login with invalid credentials
- Test token expiration

### Restaurant Management Testing
- Create restaurants as owner
- Update restaurant details
- Search restaurants
- Pagination testing

### Meal Management Testing
- Create meals for restaurants
- Search meals by name/description
- Filter meals by price range
- Update meal details

### Order Management Testing
- Place orders with different configurations
- Test order status transitions
- Filter orders by status and date
- Test order cancellation

### Admin Operations Testing
- User management (CRUD operations)
- Restaurant blocking/unblocking
- Coupon creation and management
- System-wide monitoring

## 🛠️ Tips for Testing

1. **Start with Authentication**: Always get valid tokens first
2. **Use Environment Variables**: Keep tokens and IDs in environment variables
3. **Test Error Scenarios**: Try invalid inputs, unauthorized access, etc.
4. **Check Response Codes**: Verify correct HTTP status codes
5. **Validate Response Data**: Ensure response structure matches expectations
6. **Test Pagination**: Verify pagination works correctly
7. **Test Filtering**: Ensure date and status filters work as expected

## 🔍 Common Testing Patterns

### 1. CRUD Operations
```bash
# Create → Read → Update → Delete
POST /api/restaurants/create → GET /api/restaurants/get/{id} → PUT /api/restaurants/edit/{id} → DELETE /api/restaurants/delete/{id}
```

### 2. Authorization Testing
```bash
# Test with different roles
Customer Token → Should fail for owner operations
Owner Token → Should work for owner operations
Admin Token → Should work for all operations
```

### 3. Pagination Testing
```bash
# Test different page sizes and numbers
page=0&size=5
page=1&size=10
page=0&size=100
```

### 4. Filtering Testing
```bash
# Test different filter combinations
status=PLACED
from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
status=PLACED&from=2024-01-01T00:00:00&to=2024-12-31T23:59:59
```

## 📝 Notes

- All collections use the same base URL variable
- Tokens should be updated after each login
- Entity IDs should be captured from responses and set in environment variables
- Some endpoints require specific roles (check authorization headers)
- Date formats should be ISO 8601 (e.g., `2024-01-01T00:00:00`)

## 🆘 Troubleshooting

### Common Issues:
1. **401 Unauthorized**: Check if token is valid and not expired
2. **403 Forbidden**: Verify you're using the correct role token
3. **404 Not Found**: Check if entity IDs are correct
4. **400 Bad Request**: Verify request body format and required fields

### Debug Steps:
1. Check environment variables are set correctly
2. Verify base URL is accessible
3. Ensure tokens are valid and not expired
4. Check request body format matches API specification
5. Verify entity IDs exist in the system

## 🔄 Recent Updates

### URL Structure Changes
- **Create operations**: Now use `/create` suffix (e.g., `/api/restaurants/create`)
- **Read operations**: Now use `/get` suffix (e.g., `/api/restaurants/get`)
- **Update operations**: Now use `/edit` suffix (e.g., `/api/restaurants/edit/{id}`)
- **Delete operations**: Now use `/delete` suffix (e.g., `/api/restaurants/delete/{id}`)

### Technical Enhancements
- **@Autowired**: Replaced `@RequiredArgsConstructor` with `@Autowired` for better flexibility
- **JPA Specifications**: Replaced `@Query` annotations with type-safe JPA Specifications
- **Enhanced JPA Config**: Added support for H2, MySQL, and PostgreSQL with optimized settings
- **Improved Data Initialization**: Enhanced error handling and validation in sample data loading
- **Repository Enhancements**: Extended repositories with `JpaSpecificationExecutor` for dynamic queries

### New Features
- **Database Flexibility**: Support for multiple database types (H2, MySQL, PostgreSQL)
- **Type-Safe Queries**: JPA Specifications for complex, dynamic queries
- **Better Error Handling**: Comprehensive validation and error reporting
- **Performance Optimizations**: Database-specific configurations for optimal performance
