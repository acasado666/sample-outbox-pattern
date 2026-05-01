package com.kodebytes.acasado.service;

import com.kodebytes.acasado.dto.CreateOrderRequest;
import com.kodebytes.acasado.entity.OrderEntity;
import com.kodebytes.acasado.entity.OutboxEventEntity;
import com.kodebytes.acasado.events.OrderCreatedEvent;
import com.kodebytes.acasado.mapper.OrderMapper;
import com.kodebytes.acasado.repository.OrderRepository;
import com.kodebytes.acasado.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final String orderEventsTopic;

    public OrderService(
            OrderRepository orderRepository,
            OutboxRepository outboxRepository,
            @Value("${app.outbox.topic}")
            String orderEventsTopic
    ) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.orderEventsTopic = orderEventsTopic;
    }

    @Transactional
    public UUID createOrder(CreateOrderRequest request) {

        //save the order in the database
        OrderEntity order = orderRepository.save(OrderMapper.toEntity(request));

        // Create outbox event for the created order
        OrderCreatedEvent orderCreatedEvent = OrderMapper.toEvent(order);
        OutboxEventEntity entity = OrderMapper.toOutboxEvent(order, orderEventsTopic, orderCreatedEvent);

        // Save the outbox event in the same transaction as the order creation
        outboxRepository.save(entity);

        return order.getId();
    }
}
