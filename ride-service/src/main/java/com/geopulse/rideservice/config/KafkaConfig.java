package com.geopulse.rideservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // Topic where Ride Service Publishes Request
    // Matching Service Subscriber to this Topic

    @Bean
    public NewTopic rideRequestedTopic() {
        return TopicBuilder
                .name("ride.requested")
                .partitions(3)
                .replicas(1)
                .build();
    }

    // Topic where Matching Service Publishes Result
    // Ride Service Subscriber to this Topic

    @Bean
    public NewTopic rideMatchedTopic() {
        return TopicBuilder
                .name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
