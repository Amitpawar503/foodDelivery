package com.food.delivery.util;

import com.food.delivery.dto.CustomPageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public final class ApiResponseUtil {

    private ApiResponseUtil() {
        // Utility class - prevent instantiation
    }

    public static <T> ResponseEntity<Map<String, Object>> success(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<Map<String, Object>> success(T data) {
        return success(data, "Operation completed successfully");
    }

    public static ResponseEntity<Map<String, Object>> success(String message) {
        return success(null, message);
    }

    public static ResponseEntity<Map<String, Object>> created(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public static ResponseEntity<Map<String, Object>> created(Object data) {
        return created(data, "Resource created successfully");
    }

    public static ResponseEntity<Map<String, Object>> noContent(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    public static ResponseEntity<Map<String, Object>> noContent() {
        return noContent("Resource deleted successfully");
    }

    public static <T> ResponseEntity<Map<String, Object>> paginated(Page<T> page, CustomPageRequest pageRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", page.getContent());
        response.put("pagination", createPaginationInfo(page, pageRequest));
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<Map<String, Object>> error(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("error", status.getReasonPhrase());
        response.put("status", status.value());
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(response);
    }

    public static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Map<String, Object>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<Map<String, Object>> internalServerError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static Map<String, Object> createPaginationInfo(Page<?> page, CustomPageRequest pageRequest) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", pageRequest.getPage());
        pagination.put("size", pageRequest.getSize());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("first", page.isFirst());
        pagination.put("last", page.isLast());
        pagination.put("numberOfElements", page.getNumberOfElements());
        pagination.put("sort", pageRequest.getSort() + "," + pageRequest.getDirection());
        return pagination;
    }

    public static Map<String, Object> createErrorResponse(String message, String error, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("error", error);
        response.put("status", status);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    public static Map<String, Object> createSuccessResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}
