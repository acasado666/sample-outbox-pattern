# Outbox Kafka Demo

Transactional outbox pattern demo using Java 21, Spring Boot 4, H2, and Kafka.
This project demonstrates the implementation of the transactional outbox pattern with Spring Boot 4, leveraging Kafka for event-driven communication and H2 as an in-memory database for simplicity.

## What this demonstrates

The order creation flow writes two records in one database transaction:

1. `orders` business row
2. `outbox_events` event row
   A scheduled relay reads pending rows from `outbox_events`, publishes them to Kafka, and marks them as `SENT` after Kafka acknowledges the send.
3. `idempotent` consumer also writes to its own database, your database write must still be idempotent
   
## Requirements

- Java 21
- Maven
- Docker / Docker Compose

## Run Kafka

```bash
docker compose up -d
```

## Run the application

```bash
mvn spring-boot:run
```

## Create an order

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"cust-123","amount":49.90}'
```

## H2 console

Open:

```text
http://localhost:8080/h2-console
```

Use:

```text
JDBC URL: jdbc:h2:mem:outboxdb
User: sa
Password:
```

Useful queries:

```sql
select * from orders;
select * from outbox_events;
select * from processed_messages;
select * from invoices;
```

## Idempotent
The outbox pattern gives you reliable publishing, but it usually gives at-least-once delivery, not exactly-once business effects. Spring Kafka’s listener model processes messages through @KafkaListener, and Kafka transactions can help with Kafka read-process-write flows, but when your consumer also writes to its own database, your database write must still be idempotent. 
Spring Kafka’s own docs describe Kafka EOS as applying to read → process → write flows, while the read/process side still has at-least-once characteristics.

### A consumer must assume this can happen:
1. Consumer receives OrderCreated event.
2. Consumer writes "create invoice" to its database.
3. App crashes before Kafka offset is committed.
4. Kafka delivers the same event again.
5. Consumer must not create a second invoice.

So the event needs a stable ID:
```json
{
"eventId": "5f0b4e4d-5086-42e0-82e5-df21a928f891",
"orderId": "a0b7e3f2-6a1c-4ef7-9e77-53c88e54aa01",
"customerId": "cust-123",
"amount": 49.90,
"createdAt": "2026-05-02T10:00:00Z"
}
```
The consumer stores eventId in a processed_messages table. If the ID already exists, the consumer skips the work.## File structure


## File structure

```text
outbox-kafka-demo/
├── pom.xml
├── docker-compose.yml
├── README.md
└── src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── outbox/
│   │               ├── OutboxKafkaDemoApplication.java
│   │               │
│   │               ├── kafka/
│   │               │   └── KafkaTopicConfig.java
│   │               │
│   │               ├── orders/
│   │               │   ├── OrderController.java
│   │               │   ├── OrderService.java
│   │               │   ├── OrderEntity.java
│   │               │   ├── OrderRepository.java
│   │               │   └── OrderCreatedEvent.java
│   │               │
│   │               ├── outbox/
│   │               │   ├── OutboxEvent.java
│   │               │   ├── OutboxStatus.java
│   │               │   ├── OutboxRepository.java
│   │               │   ├── OutboxRelay.java
│   │               │   └── OutboxWriter.java
│   │               │
│   │               └── billing/
│   │                   ├── consumer/
│   │                   │   └── BillingOrderConsumer.java
│   │                   │
│   │                   ├── idempotency/
│   │                   │   ├── ProcessedMessage.java
│   │                   │   └── ProcessedMessageRepository.java
│   │                   │
│   │                   └── invoice/
│   │                       ├── Invoice.java
│   │                       └── InvoiceRepository.java
│   │
│   └── resources/
│       └── application.yml
│
└── test/
└── java/
└── com/
└── example/
└── outbox/
└── OutboxKafkaDemoApplicationTests.java
```

## Production notes

This demo intentionally keeps the relay simple. In production, use a durable database such as PostgreSQL, add idempotent consumers, retry limits, dead-letter handling, metrics, cleanup for old `SENT` rows, and safe row-claiming/locking when several app instances run at the same time.
