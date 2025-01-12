package com.example.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@EnableKafka
@Component
public class CalculatorListener {

    private final CalculatorService calculatorService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(CalculatorListener.class);

    public CalculatorListener(CalculatorService calculatorService, KafkaTemplate<String, String> kafkaTemplate) {
        this.calculatorService = calculatorService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "calculator-topic", groupId = "calculator")
    public void performOperation(String message) {
        try {
            String[] parts = message.split("_");
            if (parts.length != 4) {
                logger.error("Invalid message format: {}", message);
                return;
            }

            String requestId = parts[0];
            String operation = parts[1];

            double a = Double.parseDouble(parts[2]);
            double b = Double.parseDouble(parts[3]);

            double result;
            switch (operation) {
                case "sum":
                    result = calculatorService.add(a, b);
                    break;
                case "subtraction":
                    result = calculatorService.subtract(a, b);
                    break;
                case "multiplication":
                    result = calculatorService.multiply(a, b);
                    break;
                case "division":
                    result = calculatorService.divide(a, b);
                    break;
                default:
                    logger.error("Invalid operation: {}", operation);
                    return;
            }

            // Send result to Kafka
            String resultMessage = requestId + "_" + result;
            kafkaTemplate.send("result-topic", resultMessage);

        } catch (IllegalArgumentException e) {
            logger.error("Error due to invalid input in message: {}", message, e);
        } catch (Exception e) {
            logger.error("Error occurred while processing message: {}", message, e);
        }
        finally {
            MDC.clear();
        }
    }

}
