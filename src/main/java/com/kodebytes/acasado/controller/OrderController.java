package com.kodebytes.acasado.controller;

import com.kodebytes.acasado.dto.CreateOrderRequest;
import com.kodebytes.acasado.dto.CreateOrderResponse;
import com.kodebytes.acasado.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
        UUID orderId = orderService.createOrder(request);
        return new CreateOrderResponse(orderId);
    }
}
