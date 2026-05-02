package com.kodebytes.acasado.billing;

import com.kodebytes.acasado.order.OrderCreatedEvent;
import jakarta.json.JsonException;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsListener {

    private static final Jsonb jsonb = JsonbBuilder.create();
    private static final String CONSUMER_NAME = "billing-service-demo";
    private final ProcessedMessageRepository processedMessageRepository;
    private final InvoiceRepository invoiceRepository;
    public OrderEventsListener(
            ProcessedMessageRepository processedMessageRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.processedMessageRepository = processedMessageRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @KafkaListener(
            topics = "${app.outbox.topic}", groupId = "billing-service-demo"
    )
    public void onOrderEvent(String payload) {
        System.out.println("Received order event: " + payload);

        OrderCreatedEvent event= convertStringToObject( payload,  OrderCreatedEvent.class);

        try {
            processedMessageRepository.saveAndFlush(
                    new ProcessedMessage(event.eventId(), CONSUMER_NAME)
            );
        } catch (DataIntegrityViolationException duplicate) {
            // Event already processed.
            // Do not create another invoice.
            return;
        }

        invoiceRepository.save(new Invoice(
                event.orderId(),
                event.customerId(),
                event.amount()
        ));
        // Production rule: consumers should be idempotent because the outbox
        // pattern normally gives at-least-once delivery.
    }

    private <T> T convertStringToObject(String payload, Class<T> type) {
        try {
            return jsonb.fromJson(payload, type);
        } catch (JsonException e) {
            throw new IllegalStateException("Could not deserialize order event", e);
        }
    }
}
