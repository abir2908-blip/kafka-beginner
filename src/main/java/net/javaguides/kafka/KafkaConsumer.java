package net.javaguides.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = KafkaTopicConfig.TOPIC)
    public void receiveMessage(String message) {
        log.info("Consumer received: {}", message);
    }
}
