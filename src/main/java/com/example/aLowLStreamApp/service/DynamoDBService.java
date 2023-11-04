package com.example.aLowLStreamApp.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DynamoDBService {
    private final AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().build();
    private final String connectionTable = "toilet-cubicle-system-dev-ConnectionsTable-KG8ZLPHFY8E7";

    public List<String> getAllConnections(Map<String, AttributeValue> exclusiveStartKey) {
        List<String> connections = new ArrayList<>();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(connectionTable)
                .withAttributesToGet("connectionId")
                .withExclusiveStartKey(exclusiveStartKey);

        ScanResult scanResult = dynamoDB.scan(scanRequest);

        for ( Map<String, AttributeValue>  item : scanResult.getItems()) {
            AttributeValue connectionId = item.get("connectionId");
            connections.add(connectionId.getS());
        }

        Map<String, AttributeValue>  lastEvaluatedKey = scanResult.getLastEvaluatedKey();
        if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
            connections.addAll(getAllConnections(lastEvaluatedKey));
        }

        return connections;
    }
}
