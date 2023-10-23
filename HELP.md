package com.example.aLowLStreamApp;

import com.example.aLowLStreamApp.domain.SnapshotData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.apache.kafka.streams.kstream.Suppressed.untilWindowCloses;

@SpringBootApplication
public class ALowLStreamAppApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ALowLStreamAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        final String bootstrapServers = "localhost:9093";
        final String inputTopic = "alowl";


        // Configure the Streams application.
        final Properties streamsConfiguration = getStreamsConfiguration(bootstrapServers);

        // Define the processing topology of the Streams application.
        final StreamsBuilder builder = new StreamsBuilder();
        createStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.cleanUp();
        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private void createStream(StreamsBuilder builder) {
        KStream<String, String> myStream = builder.stream("alowl");
        Duration timeDifference = Duration.ofSeconds(3);
        Duration gracePeriod = Duration.ofSeconds(0);
        SlidingWindows slidingWindows = SlidingWindows.ofTimeDifferenceAndGrace(timeDifference, gracePeriod);
        myStream
                .groupByKey()
                .windowedBy(slidingWindows)
                .reduce((value1, value2) -> {
                    var data1 = parseJson(value1);
                    var data2 = parseJson(value2);
                    System.out.println(System.currentTimeMillis() / 1000 + " processing value1" + data1);
                    System.out.println(System.currentTimeMillis() / 1000 + "processing value2" + data2);
                    if (data1 != null && data2 != null) {
                        long timestamp1 = data1.getSnapshotTime();
                        long timestamp2 = data2.getSnapshotTime();
                        if (timestamp1 > timestamp2) {
                            return value1;
                        } else {
                            return value2;
                        }
                    } else {
                        // Handle parsing errors or missing timestamps
                        return value1; // Default to the first value
                    }
                })
                .toStream()
                .map((key, value) -> new KeyValue<>("cam01", value))
                .to("alowlOut");
    }

    private static SnapshotData parseJson(String json) {
        try {
            return new ObjectMapper().readValue(json, SnapshotData.class);
        } catch (IOException e) {
            System.out.println("Exception parsing" + e);
            e.printStackTrace();
            return null;
        }
    }

    private Properties getStreamsConfiguration(String bootstrapServers) {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(
                StreamsConfig.APPLICATION_ID_CONFIG,
                "alowl-stream-app");
        streamsConfiguration.put(
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        streamsConfiguration.put(
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        streamsConfiguration.put(
                StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());

//        // Records should be flushed every 10 seconds. This is less than the default
//        // in order to keep this example interactive.
streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
//        // For illustrative purposes we disable record caches.
//        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
// Use a temporary directory for storing state, which will be automatically removed after the test.
//        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());

        return streamsConfiguration;
    }
}



