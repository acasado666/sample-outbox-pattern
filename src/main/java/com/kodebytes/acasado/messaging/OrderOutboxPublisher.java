package com.kodebytes.acasado.messaging;

import com.kodebytes.acasado.entity.OutboxEventEntity;
import com.kodebytes.acasado.entity.OutboxStatus;
import com.kodebytes.acasado.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class OrderOutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final int batchSize;

    public OrderOutboxPublisher(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.outbox.batch-size}") int batchSize
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${app.outbox.relay-fixed-delay-ms}")
    public void publishPendingEvents() {
        // Option A: Using method naming convention (RECOMMENDED)
        List<OutboxEventEntity> events = outboxRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEventEntity event : events) {

            Optional<OutboxEventEntity> eventFound = outboxRepository.findById(event.getId());

            if (eventFound.isPresent()) {
                OutboxEventEntity outboxEvent = eventFound.get();
                try {
                    kafkaTemplate
                            .send(outboxEvent.getTopicName(), outboxEvent.getEventKey(), outboxEvent.getPayload())
                            .get(10, TimeUnit.SECONDS);

                    outboxEvent.markSent();
                } catch (Exception ex) {
                    outboxEvent.markFailed(ex.getMessage());
                }
            }
        }
    }
}





//    @Transactional
//    public void markPublished(UUID eventId) {
//        OutboxEventEntity event = outboxRepository.findById(eventId)
//                .orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + eventId));
//
//        event.markSent();
//    }
//
//    @Transactional
//    public void markFailed(UUID eventId, String error) {
//        OutboxEventEntity event = outboxRepository.findById(eventId)
//                .orElseThrow(() -> new IllegalArgumentException("Outbox event not found: " + eventId));
//
//        event.markFailed(error);
//    }
//    }
