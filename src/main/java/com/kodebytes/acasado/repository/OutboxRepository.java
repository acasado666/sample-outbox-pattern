package com.kodebytes.acasado.repository;

import com.kodebytes.acasado.entity.OutboxEventEntity;
import com.kodebytes.acasado.entity.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {

    // Option 1: Spring Data JPA method naming convention
    List<OutboxEventEntity> findByStatus(OutboxStatus status);

    // Option 2: More specific method name
    List<OutboxEventEntity> findOutboxEventEntitiesByStatus(OutboxStatus status);

    // Option 3: Custom query with ordering
    @Query("""
           select e
           from OutboxEventEntity e
           where e.status = :status
           order by e.createdAt asc
           """)
    List<OutboxEventEntity> findPendingByStatus(OutboxStatus status, Pageable pageable);

    // Option 4: Specific method for PENDING status
    @Query("""
           select e
           from OutboxEventEntity e
           where e.status = com.example.outbox.entity.OutboxStatus.PENDING
           order by e.createdAt asc
           """)
    List<OutboxEventEntity> findPending(Pageable pageable);

}
