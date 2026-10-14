package net.javaguides.kafka;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = KafkaTopicConfig.TOPIC,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@ExtendWith(OutputCaptureExtension.class)
@DirtiesContext
class KafkaMessageIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void sendsMessageThroughHttpAndKafkaToConsumer(CapturedOutput output) throws Exception {
        String message = "Hello Kafka from the integration test!";

        HttpResponse<String> response = postMessage(message);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Message sent to Kafka: " + message);
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                assertThat(output.getOut()).contains("Consumer received: " + message));
    }

    @Test
    void rejectsBlankMessage() throws Exception {
        assertThat(postMessage("   ").statusCode()).isEqualTo(400);
    }

    private HttpResponse<String> postMessage(String message) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/messages"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
