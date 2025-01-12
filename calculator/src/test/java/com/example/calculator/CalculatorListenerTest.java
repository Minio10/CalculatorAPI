package com.example.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorListenerTest {

    @Mock
    private CalculatorService calculatorService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private CalculatorListener calculatorListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks
    }

    @Test
    public void testPerformOperation_sum() {
        // Given
        String message = "requestId_sum_2.0_3.0";
        when(calculatorService.add(2.0, 3.0)).thenReturn(5.0);

        // When
        calculatorListener.performOperation(message);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("result-topic"), captor.capture());
        assertEquals("requestId_5.0", captor.getValue());
    }

    @Test
    public void testPerformOperation_subtraction() {
        // Given
        String message = "requestId_subtraction_5.0_3.0";
        when(calculatorService.subtract(5.0, 3.0)).thenReturn(2.0);

        // When
        calculatorListener.performOperation(message);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("result-topic"), captor.capture());
        assertEquals("requestId_2.0", captor.getValue());
    }

    @Test
    public void testPerformOperation_invalidMessageFormat() {
        // Given
        String message = "invalid_message";

        // When
        calculatorListener.performOperation(message);

        // Then
        // Verify no message is sent to Kafka for an invalid format
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    public void testPerformOperation_invalidOperation() {
        // Given
        String message = "requestId_invalid_2.0_3.0";

        // When
        calculatorListener.performOperation(message);

        // Then
        // Verify no message is sent to Kafka for an invalid operation
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
