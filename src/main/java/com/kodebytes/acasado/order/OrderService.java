package com.kodebytes.acasado.order;

import com.kodebytes.acasado.outbox.OutboxEventEntity;
import com.kodebytes.acasado.outbox.OutboxRepository;
import jakarta.json.JsonException;
import jakarta.json.bind.JsonbBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final String orderEventsTopic;

    public OrderService(OrderRepository orderRepository,
                        OutboxRepository outboxRepository,
                        @Value("${app.outbox.topic}")
                        String orderEventsTopic) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.orderEventsTopic = orderEventsTopic;
    }

    @Transactional
    public UUID createOrder(CreateOrderRequest request) {

        //save the order in the database
        OrderEntity order = orderRepository.save(new OrderEntity(request.customerId(), request.amount()));

        // Create order created event for the created order
        UUID eventID = UUID.randomUUID();
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                eventID,
                order.getId(),
                order.getCustomerId(),
                order.getAmount(),
                order.getCreatedAt()
        );

        OutboxEventEntity entity = new OutboxEventEntity(
                "Order",
                order.getId().toString(),
                "OrderCreated",
                orderEventsTopic,
                order.getId().toString(),
                jsonPayload(orderCreatedEvent)
        );

        // Save the outbox event in the same transaction as the order creation
        outboxRepository.save(entity);

        return order.getId();
    }

    private String jsonPayload(Object value) {
        try {
            return JsonbBuilder.create().toJson(value);
        } catch (JsonException e) {
            throw new IllegalStateException("Could not serialize outbox event", e);
        }
    }
}

