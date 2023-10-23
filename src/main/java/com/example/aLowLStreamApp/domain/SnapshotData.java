package com.example.aLowLStreamApp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SnapshotData {
    private String bucketName;

    private String key;

    @JsonProperty("isThereAPerson")
    private boolean isThereAPerson;

    private long snapshotTime;
}
