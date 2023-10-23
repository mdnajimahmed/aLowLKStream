package com.example.aLowLStreamApp.util;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;


public class Util {
    public static Properties getStreamsConfiguration(String bootstrapServers, String appId, boolean useIam) {
        System.out.println("building config with setting " + useIam);
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(
                StreamsConfig.APPLICATION_ID_CONFIG,
                appId);
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
        streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 3 * 1000);
//        // For illustrative purposes we disable record caches.
//        streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        // Use a temporary directory for storing state, which will be automatically removed after the test.
//        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getAbsolutePath());

        if (useIam) {
            streamsConfiguration.put(
                    StreamsConfig.SECURITY_PROTOCOL_CONFIG,
                    "SASL_SSL");
            streamsConfiguration.put("sasl.mechanism", "AWS_MSK_IAM");
            streamsConfiguration.put("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required;");
            streamsConfiguration.put("sasl.client.callback.handler.class", "software.amazon.msk.auth.iam.IAMClientCallbackHandler");
        }

        return streamsConfiguration;
    }
}
