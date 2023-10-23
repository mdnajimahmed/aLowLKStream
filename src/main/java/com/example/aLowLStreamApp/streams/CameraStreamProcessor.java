package com.example.aLowLStreamApp.streams;

import com.example.aLowLStreamApp.domain.SnapshotData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CameraStreamProcessor extends ALowLStream {
    private SnapshotData parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, SnapshotData.class);
        } catch (IOException e) {
            System.out.println("Exception parsing" + e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void createStream(StreamsBuilder builder, String source, String destination) {
        KStream<String, String> myStream = builder.stream(source);
        myStream
                .groupByKey()
                .reduce((oldRecord, newRecord) -> {
                    var data1 = parseJson(oldRecord);
                    var data2 = parseJson(newRecord);
                    System.out.println(System.currentTimeMillis() / 1000 + " processing oldRecord" + data1);
                    System.out.println(System.currentTimeMillis() / 1000 + "processing newRecord" + data2);
                    if (data1 != null && data2 != null) {
                        long timestamp1 = data1.getSnapshotTime();
                        long timestamp2 = data2.getSnapshotTime();
                        if (timestamp1 > timestamp2) {
                            return oldRecord;
                        } else {
                            return newRecord;
                        }
                    } else {
                        // Handle parsing errors or missing timestamps
                        return oldRecord; // Default to the first value
                    }
                })
                .toStream()
                .mapValues(this::parseJson)
                .map((key, value) -> new KeyValue<>("cam01", String.valueOf(value.isThereAPerson())))
                .to(destination);
    }

}
