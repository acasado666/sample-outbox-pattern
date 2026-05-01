package com.kodebytes.acasado.mapper;

import com.kodebytes.acasado.dto.CreateOrderRequest;
import com.kodebytes.acasado.entity.OrderEntity;
import com.kodebytes.acasado.entity.OutboxEventEntity;
import com.kodebytes.acasado.events.OrderCreatedEvent;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.JsonException;


public class OrderMapper {

    public static OrderEntity toEntity(CreateOrderRequest request) {
        return new OrderEntity(request.customerId(), request.amount());
    }

    public static OrderCreatedEvent toEvent(OrderEntity order) {
        return new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getAmount(),
                order.getCreatedAt()
        );
    }

    public static OutboxEventEntity toOutboxEvent(OrderEntity order, String orderEventsTopic, OrderCreatedEvent orderCreatedEvent) {
        return new OutboxEventEntity(
                "Order",
                order.getId().toString(),
                "OrderCreated",
                orderEventsTopic,
                order.getId().toString(),
                jsonPayload(orderCreatedEvent)
        );
    }


    private static final Jsonb jsonb = JsonbBuilder.create();

    private static String jsonPayload(Object value) {
        try {
            return jsonb.toJson(value);
        } catch (JsonException e) {
            throw new IllegalStateException("Could not serialize outbox event", e);
        }
    }
}
