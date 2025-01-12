package com.example.rest;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OperatorService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<String>> responseMap = new ConcurrentHashMap<>();

    public OperatorService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String performOperation(String operation, double a, double b, String requestId) {
        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        responseMap.put(requestId, responseFuture);

        // Include operation in the message
        String message = requestId + "_" + operation + "_" + a + "_" + b;
        kafkaTemplate.send(new ProducerRecord<>("calculator-topic", message));

        try {
            return responseFuture.join();
        } finally {
            responseMap.remove(requestId);
        }
    }

    @KafkaListener(topics = "result-topic", groupId = "rest")
    public void listenResponse(String message) {
        String[] parts = message.split("_");
        String requestId = parts[0];
        String result = parts[1];

        CompletableFuture<String> responseFuture = responseMap.get(requestId);
        if (responseFuture != null) {
            responseFuture.complete(result);
        }
    }
}
