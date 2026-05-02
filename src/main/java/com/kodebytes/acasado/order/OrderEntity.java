package com.kodebytes.acasado.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String customerId;

    private BigDecimal amount;

    private Instant createdAt;

    protected OrderEntity() {
    }

    public OrderEntity(String customerId, BigDecimal amount) {
        this.customerId = customerId;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
