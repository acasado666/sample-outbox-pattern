package com.kodebytes.acasado.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OutboxRepositoryWriter {

    private final OutboxRepository outboxRepository;

    public OutboxRepositoryWriter(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void markSent(UUID eventId) {
        OutboxEventEntity event = outboxRepository.findById(eventId)
                .orElseThrow();

        event.markSent();
    }

    @Transactional
    public void markFailed(UUID eventId, String error) {
        OutboxEventEntity event = outboxRepository.findById(eventId)
                .orElseThrow();

        event.markFailed(error);
    }
}