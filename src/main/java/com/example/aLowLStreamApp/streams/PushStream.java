package com.example.aLowLStreamApp.streams;

import com.example.aLowLStreamApp.domain.CubicleStatus;
import com.example.aLowLStreamApp.domain.CubicleStatusRepository;
import com.example.aLowLStreamApp.service.ApiGatewayMessageSender;
import com.example.aLowLStreamApp.service.DynamoDBService;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Service;

@Service
public class PushStream extends ALowLStream {
    private final CubicleStatusRepository repository;
    private final DynamoDBService dynamoDBService;
    private final ApiGatewayMessageSender apiGatewayMessageSender;


    public PushStream(CubicleStatusRepository repository, DynamoDBService dynamoDBService, ApiGatewayMessageSender apiGatewayMessageSender) {
        this.repository = repository;
        this.dynamoDBService = dynamoDBService;
        this.apiGatewayMessageSender = apiGatewayMessageSender;
    }

    @Override
    protected void createStream(StreamsBuilder builder, String source, String destination) {
        System.out.println("Creating redis push stream" + source);
        KStream<String, String> stream = builder.stream(source);
        // Push data to Redis
        stream.foreach(this::push);
    }

    private void push(String key, String value) {
        pushToRedis(key, value);
        pushToWeb(key, value);

    }

    private void pushToRedis(String key, String value) {
        try {
            System.out.println("pushing to redis");
            CubicleStatus cubicleStatus = new CubicleStatus();
            cubicleStatus.setId(key);
            cubicleStatus.setStatus(Boolean.parseBoolean(value));
            repository.save(cubicleStatus);
        } catch (Exception e) {
            System.out.println("error pushing to redis");
            e.printStackTrace();
        }
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

    private void sendPushNotification(String connection, String key, boolean status) {
        System.out.println("sending push notification to " + connection);
        apiGatewayMessageSender.sendMessage(connection, String.format("[%s,%s]", key, status));
    }
}
