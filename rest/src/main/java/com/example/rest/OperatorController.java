package com.example.rest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperatorController {

    private static final Logger logger = LoggerFactory.getLogger(OperatorController.class);

    private final OperatorService operatorService;

    private static final List<String> VALID_OPERATIONS = Arrays.asList("sum", "subtraction", "multiplication", "division");

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping("api/{operation}")
    public ResponseEntity<Map<String, Object>> handleOperation(
            @PathVariable String operation,
            @RequestParam(value = "a", required = false) Double a,  // Handle missing parameter by making it optional
            @RequestParam(value = "b", required = false) Double b) {

        // Validate the operation and input
        ResponseEntity<Map<String, Object>> validationError = validateInputs(operation, a, b);
        if (validationError != null) {
            return validationError;
        }

        logger.info("Received {} request with a = {}, b = {}", operation, a, b);

        return processOperation(operation, a, b);
    }

    private ResponseEntity<Map<String, Object>> processOperation(String operation, double a, double b) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        logger.info("Processing '{}' operation with operands: a = {}, b = {}", operation, a, b);

        try {
            String result = operatorService.performOperation(operation, a, b, requestId);

            logger.info("Operation '{}' completed successfully. Result: {}", operation, result);

            return createSuccessResponse(result, requestId);
        } catch (Exception e) {
            logger.error("Failed to process '{}' operation with operands: a = {}, b = {}", operation, a, b, e);

            return createErrorResponse(
                    "Calculation failed",
                    String.format("An error occurred while performing '%s' operation. Please check the inputs and operation.", operation)
            );
        } finally {
            MDC.clear();
        }
    }


    private ResponseEntity<Map<String, Object>> createErrorResponse(String error, String message) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", error);
        errorBody.put("message", message);
        return ResponseEntity.badRequest().body(errorBody);
    }

    private ResponseEntity<Map<String, Object>> validateInputs(String operation, Double a, Double b) {
        if (a == null || b == null) {
            logger.warn("Missing inputs for the '{}'", operation);
            return createErrorResponse("Missing parameters", "Parameters 'a' and 'b' are required.");
        }
        if (!VALID_OPERATIONS.contains(operation)) {
            logger.warn("Invalid operation '{}'", operation);
            return createErrorResponse("Operation not valid", "Valid operations are: " + VALID_OPERATIONS);
        }

        if ("division".equals(operation) && b == 0) {
            logger.warn("Invalid input b: {} for division", b);
            return createErrorResponse("Invalid input for division", "Value of 'b' cannot be zero.");
        }

        return null;
    }

    private ResponseEntity<Map<String, Object>> createSuccessResponse(String result, String requestId) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("result", result);

        return ResponseEntity.ok()
                .header("X-Request-ID", requestId)
                .body(responseBody);
    }
}
