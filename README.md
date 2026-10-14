# Kafka for beginners

A standalone Spring Boot project with one producer and one consumer in the same app.
It uses Java 25 and Spring Boot 4.1.1, matching the parent folder's project.

```text
curl → MessageController → KafkaProducer → Kafka topic → KafkaConsumer → console
```

- **Producer** sends a message to Kafka.
- **Topic** (`beginner-messages`) stores the messages.
- **Consumer** listens for messages and prints them.
- **Broker** is the Kafka server, running in Docker.

## 1. Prerequisites

- JDK 25 or newer (`java -version`).
- Docker Desktop installed and running (`docker compose version`).
- Maven is included through `mvnw`; the first run needs internet to download dependencies.

In IntelliJ, open this folder's `pom.xml` as a new project, choose a JDK 25+
Project SDK, and let Maven finish importing.

## 2. Start Kafka

From this folder (`cd kafka-beginner` if you are in `springboot-first-app`):

```bash
docker compose up -d --wait
```

This starts one Kafka broker on `localhost:9092`. The official image's default
configuration runs Kafka without ZooKeeper. This setup is for local learning.

## 3. Start Spring Boot

```bash
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.
Alternatively, run `KafkaBeginnerApplication.main()` in IntelliJ.
Wait for the `Started KafkaBeginnerApplication` log. The app uses port **8081**.
Spring creates the `beginner-messages` topic automatically on startup.

## 4. Send a message

In a second terminal:

```bash
curl -X POST http://localhost:8081/api/messages \
  -H 'Content-Type: text/plain' \
  --data-raw 'Hello Kafka!'
```

The HTTP response is:

```text
Message sent to Kafka: Hello Kafka!
```

In the Spring Boot console, look for:

```text
Consumer received: Hello Kafka!
```

Send another message by changing the text. The HTTP response confirms that Kafka
accepted the message; the consumer processes it separately. Blank messages return
HTTP 400. A failed Kafka send does not return the success response.

## 5. Read the code in this order

1. `MessageController.java`: receives the HTTP request and calls the producer.
2. `KafkaProducer.java`: uses Spring's `KafkaTemplate` to send the text.
3. `KafkaTopicConfig.java`: creates a topic with one partition and one replica.
4. `KafkaConsumer.java`: `@KafkaListener` receives messages and logs them.
5. `application.properties`: broker address, string serialization, and consumer group.

The `beginner-group` consumer group remembers its position in the topic. The
`earliest` setting reads existing messages when the group has no saved position;
it does not replay every message each time the app restarts.

## 6. Run the tests

```bash
./mvnw test
```

The integration test starts an embedded Kafka broker on a random port, sends an
HTTP request, and checks that the real consumer logs the message. It also checks
blank-message validation. Docker is not needed for the tests.

## 7. Stop

Press Ctrl+C in the Spring Boot terminal, then run:

```bash
docker compose down
```

The demo has no persistent Docker volume. Removing the container removes its
Kafka messages.

## Troubleshooting

- **`docker: command not found`**: install Docker Desktop and start it.
- **Cannot connect to Kafka**: run `docker compose ps` and `docker compose logs kafka`.
  Kafka should be healthy before starting the app.
- **Port 9092 is busy**: stop the other local Kafka instance before starting this one.
- **Port 8081 is busy**: change `server.port` and use that port in the curl command.
- **Unsupported Java release**: set `JAVA_HOME` and IntelliJ's Maven runner JRE to JDK 25+.

## References

- [Spring Boot Kafka support](https://docs.spring.io/spring-boot/reference/messaging/kafka.html)
- [Spring Kafka quick tour](https://docs.spring.io/spring-kafka/reference/quick-tour.html)
- [Apache Kafka Docker guide](https://kafka.apache.org/41/getting-started/docker/)
