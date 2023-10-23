package com.example.aLowLStreamApp;

import com.example.aLowLStreamApp.streams.CameraStreamProcessor;
import com.example.aLowLStreamApp.streams.PushStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ALowLStreamAppApplication implements CommandLineRunner {
    private final CameraStreamProcessor cameraStreamProcessor;
    private final PushStream pushStream;

    @Value("${app.kafka.bootstrapServer}")
    private String bootstrapServer;

    public ALowLStreamAppApplication(CameraStreamProcessor cameraStreamProcessor, PushStream pushStream) {
        this.cameraStreamProcessor = cameraStreamProcessor;
        this.pushStream = pushStream;
    }

    public static void main(String[] args) {
        SpringApplication.run(ALowLStreamAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        cameraStreamProcessor.start("cameraStreamProcessor", bootstrapServer, "alowl", "alowlOut");
        System.out.println("Starting redisPushStream");
        pushStream.start("redisPushStream", bootstrapServer, "alowlOut", null);
        System.out.println("Starting webPushStream");
    }
}



