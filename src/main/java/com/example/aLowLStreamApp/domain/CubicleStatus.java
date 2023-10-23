package com.example.aLowLStreamApp.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash
public class CubicleStatus {
    @Id
    private String id;

    private Boolean status;
}
