package com.example.aLowLStreamApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@Slf4j
public class DummyPushScheduler {

    private final ApiGatewayMessageSender apiGatewayMessageSender;

    private final DynamoDBService dynamoDBService;

    private int trueCount = 0;
    Random r = new Random();

    public DummyPushScheduler(ApiGatewayMessageSender apiGatewayMessageSender, DynamoDBService dynamoDBService) {
        this.apiGatewayMessageSender = apiGatewayMessageSender;
        this.dynamoDBService = dynamoDBService;
    }

    @Scheduled(fixedRate = 3000)
    public void reportCurrentTime() {
        log.info("The time is now {}", LocalDateTime.now());
        boolean status = trueCount > 0;
        // randomly starts true session
        // once starts maintains for 20 seconds and then goes off and then randomly starts again.

        if (!status) {
            status = r.nextInt(4) == 0;
            trueCount = status ? 1 : 0; // false to true
        } else {
            if (trueCount > 7) {
                status = false;
                trueCount = 0;
            } else {
                status = true;
                trueCount++;
            }
        }

        pushToWeb("cam01", String.valueOf(status));
    }

    private void sendPushNotification(String connection, String key, boolean status) {
        System.out.println("sending push notification to " + connection);
        apiGatewayMessageSender.sendMessage(connection, String.format("[%s,%s]", key, status));
    }

    private void pushToWeb(String key, String value) {
        try {
            boolean status = Boolean.parseBoolean(value);
            dynamoDBService.getAllConnections(null).forEach(connection -> sendPushNotification(connection, key, status));
        } catch (Exception e) {
            System.out.println("error pushing to web");
            e.printStackTrace();
        }

    }
}
