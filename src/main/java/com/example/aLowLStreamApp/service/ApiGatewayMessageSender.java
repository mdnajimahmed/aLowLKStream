package com.example.aLowLStreamApp.service;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.apigateway.model.EndpointConfiguration;
import com.amazonaws.services.apigatewaymanagementapi.AmazonApiGatewayManagementApi;
import com.amazonaws.services.apigatewaymanagementapi.AmazonApiGatewayManagementApiClient;
import com.amazonaws.services.apigatewaymanagementapi.AmazonApiGatewayManagementApiClientBuilder;
import com.amazonaws.services.apigatewaymanagementapi.model.PostToConnectionRequest;
import com.amazonaws.services.apigatewaymanagementapi.model.PostToConnectionResult;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
public class ApiGatewayMessageSender {


    private final AmazonApiGatewayManagementApi apig;
    private static final String API_ENDPOINT = "https://3iy02674sg.execute-api.ap-southeast-1.amazonaws.com/dev";

    public ApiGatewayMessageSender() {

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(API_ENDPOINT, "ap-southeast-1");
        apig = AmazonApiGatewayManagementApiClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    public void sendMessage(String connectionId, String body) {
        try {
            ByteBuffer data = ByteBuffer.wrap(body.getBytes(StandardCharsets.UTF_8));
            PostToConnectionRequest request = new PostToConnectionRequest()
                    .withConnectionId(connectionId)
                    .withData(data);
            PostToConnectionResult result = apig.postToConnection(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
