package com.kodebytes.acasado.outbox;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPollingPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${app.outbox.batch-size}")
    private int batchSize;
    private final OutboxRepositoryWriter outboxWriter;


    public OutboxPollingPublisher(
            OutboxRepository outboxRepository,
            OutboxRepositoryWriter outboxWriter,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.outboxWriter = outboxWriter;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${app.outbox.relay-fixed-delay-ms}")
    public void publishPendingEvents() {
        List<OutboxEventEntity> events = outboxRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEventEntity event : events) {
            try {
                kafkaTemplate
                        .send(event.getTopicName(), event.getEventKey(), event.getPayload())
                        .get(10, TimeUnit.SECONDS);

                outboxWriter.markSent(event.getId());
            } catch (Exception ex) {
                outboxWriter.markFailed(event.getId(), ex.getMessage());
            }
        }
    }
}
