package net.javaguides.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TOPIC = "beginner-messages";

    @Bean
    public NewTopic beginnerMessagesTopic() {
        // One partition and one copy are enough for our single local Kafka broker.
        return TopicBuilder.name(TOPIC).partitions(1).replicas(1).build();
    }
}
