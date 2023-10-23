package com.example.aLowLStreamApp.streams;

import com.example.aLowLStreamApp.util.Util;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;

import java.util.Properties;

public abstract class ALowLStream {
    public void start(String appId,String bootstrapServers, String source, String destination) {
        final Properties streamsConfiguration = Util.getStreamsConfiguration(bootstrapServers,appId);
        final StreamsBuilder builder = new StreamsBuilder();
        createStream(builder,source,destination);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    protected abstract void createStream(StreamsBuilder builder, String source,String destination);
}
