package com.kodebytes.acasado.billing;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {

    @Id
    private UUID eventId;

    private String consumerName;

    private Instant processedAt;

    protected ProcessedMessage() {
    }

    public ProcessedMessage(UUID eventId, String consumerName) {
        this.eventId = eventId;
        this.consumerName = consumerName;
        this.processedAt = Instant.now();
    }
}