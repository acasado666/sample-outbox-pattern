package com.kodebytes.acasado.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsListener {

    @KafkaListener(
            topics = "${app.outbox.topic}", groupId = "billing-service-demo"
    )
    public void onOrderEvent(String payload) {
        System.out.println("Received order event: " + payload);

        // Production rule: consumers should be idempotent because the outbox
        // pattern normally gives at-least-once delivery.
    }
}
