package com.example.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void testAddition() {
        assertEquals(5.0, calculatorService.add(2.0, 3.0));
    }

    @Test
    void testSubtraction() {
        assertEquals(1.0, calculatorService.subtract(3.0, 2.0));
    }

    @Test
    void testMultiplication() {
        assertEquals(6.0, calculatorService.multiply(2.0, 3.0));
    }

    @Test
    void testDivision() {
        assertEquals(2.0, calculatorService.divide(6.0, 3.0));
    }
}
