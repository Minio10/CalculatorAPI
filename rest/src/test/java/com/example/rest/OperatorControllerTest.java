package com.example.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.notNullValue;

public class OperatorControllerTest {

    @Mock
    private OperatorService operatorService;

    @InjectMocks
    private OperatorController operatorController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(operatorController).build();
    }

    @Test
    public void testHandleOperation_sum_success() throws Exception {
        // Given
        String operation = "sum";
        double a = 2.0;
        double b = 3.0;

        // Use argument matchers for all method arguments
        when(operatorService.performOperation(eq(operation), eq(a), eq(b), any(String.class)))
                .thenReturn("5.0");

        // When & Then
        mockMvc.perform(get("/api/{operation}", operation)
                        .param("a", String.valueOf(a))
                        .param("b", String.valueOf(b)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("5.0"))
                .andExpect(header().string("X-Request-ID", notNullValue()));
    }

    @Test
    public void testHandleOperation_invalidOperation() throws Exception {
        // Given
        String operation = "invalidOp";
        double a = 2.0;
        double b = 3.0;

        // When & Then
        mockMvc.perform(get("/api/{operation}", operation)
                        .param("a", String.valueOf(a))
                        .param("b", String.valueOf(b)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Operation not valid"))
                .andExpect(jsonPath("$.message").value("Valid operations are: [sum, subtraction, multiplication, division]"));
    }

    @Test
    public void testHandleOperation_divisionByZero() throws Exception {
        // Given
        String operation = "division";
        double a = 5.0;
        double b = 0.0;

        // When & Then
        mockMvc.perform(get("/api/{operation}", operation)
                        .param("a", String.valueOf(a))
                        .param("b", String.valueOf(b)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid input for division"))
                .andExpect(jsonPath("$.message").value("Value of 'b' cannot be zero."));
    }

    @Test
    public void testHandleOperation_internalServerError() throws Exception {
        // Given
        String operation = "sum";
        double a = 2.0;
        double b = 3.0;

        // Use matchers for all arguments
        when(operatorService.performOperation(eq(operation), eq(a), eq(b), any(String.class)))
                .thenThrow(new RuntimeException("Internal error"));

        // When & Then
        mockMvc.perform(get("/api/{operation}", operation)
                        .param("a", String.valueOf(a))
                        .param("b", String.valueOf(b)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Calculation failed"))
                .andExpect(jsonPath("$.message").value("An error occurred while performing 'sum' operation. Please check the inputs and operation."));
    }

    @Test
    public void testHandleOperation_missingParameters() throws Exception {
        // Simulate a request with missing 'a' and 'b' parameters
        mockMvc.perform(get("/api/sum")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Expecting a Bad Request status
                .andExpect(jsonPath("$.error").value("Missing parameters"))
                .andExpect(jsonPath("$.message").value("Parameters 'a' and 'b' are required."));
    }
}
