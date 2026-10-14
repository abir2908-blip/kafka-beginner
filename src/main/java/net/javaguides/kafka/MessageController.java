package net.javaguides.kafka;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final KafkaProducer producer;

    public MessageController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public CompletableFuture<String> sendMessage(@RequestBody String message) {
        if (message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must not be blank");
        }

        // Report success only after Kafka acknowledges the message.
        return producer.sendMessage(message)
                .thenApply(result -> "Message sent to Kafka: " + message);
    }
}
