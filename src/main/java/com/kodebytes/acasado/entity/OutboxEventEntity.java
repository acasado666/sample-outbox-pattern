package com.kodebytes.acasado.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String topicName;

    private String eventKey;

    @Lob
    @Column(columnDefinition = "CLOB", nullable = false)
    private String payload;



    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private int attempts;

    private Instant createdAt;

    private Instant sentAt;

    @Column(columnDefinition = "CLOB")
    private String lastError;

    @Version
    private long version;

    protected OutboxEventEntity() {
    }

    public OutboxEventEntity(
            String aggregateType,
            String aggregateId,
            String eventType,
            String topicName,
            String eventKey,
            String payload
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topicName = topicName;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(String error) {
        this.status = OutboxStatus.PENDING;
        this.attempts++;
        this.lastError = error;
    }

    public UUID getId() {
        return id;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getPayload() {
        return payload;
    }
    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }
}
