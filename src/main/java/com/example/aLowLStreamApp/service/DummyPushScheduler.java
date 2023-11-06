package com.example.aLowLStreamApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@Slf4j
public class DummyPushScheduler {
    private final CamStatusSender camStatusSender;
    private int trueCount = 0;
    Random r = new Random();

    public DummyPushScheduler( CamStatusSender camStatusSender) {
        this.camStatusSender = camStatusSender;
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
        camStatusSender.send("cam01", String.valueOf(status));
    }
}
