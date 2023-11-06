package com.example.aLowLStreamApp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service
public class CamStatusSender {
    private final ObjectMapper objectMapper;

    private final ApiGatewayMessageSender apiGatewayMessageSender;

    private final DynamoDBService dynamoDBService;


    public CamStatusSender(ObjectMapper objectMapper, ApiGatewayMessageSender apiGatewayMessageSender, DynamoDBService dynamoDBService) {
        this.objectMapper = objectMapper;
        this.apiGatewayMessageSender = apiGatewayMessageSender;
        this.dynamoDBService = dynamoDBService;
    }

    public void send(String camId, String statusStr){
        try {
            boolean status = Boolean.parseBoolean(statusStr);
            dynamoDBService.getAllConnections(null).forEach(connection -> sendPushNotification(connection, camId, status));
        } catch (Exception e) {
            System.out.println("error pushing to web");
            e.printStackTrace();
        }
    }

    private void sendPushNotification(String connection, String key, boolean status) {
        System.out.println("sending push notification to " + connection);
        apiGatewayMessageSender.sendMessage(connection, toJson(Arrays.asList(key, status)));
    }

    private String toJson(List<? extends Serializable> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
