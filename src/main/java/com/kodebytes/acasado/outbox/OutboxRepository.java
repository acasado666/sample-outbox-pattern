package com.kodebytes.acasado.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {

    // Option 1: Spring Data JPA method naming convention
    List<OutboxEventEntity> findByStatus(OutboxStatus status);
}
