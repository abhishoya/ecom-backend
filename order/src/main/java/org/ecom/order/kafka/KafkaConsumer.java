package org.ecom.order.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelTraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.ecom.common.config.jackson.SimpleGrantedAuthorityDeserializer;
import org.ecom.common.model.event.KafkaEvent;
import org.ecom.common.model.event.PaymentEventData;
import org.ecom.common.model.order.OrderStatus;
import org.ecom.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KafkaConsumer {

    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Tracer tracer;

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "order_payment_success", groupId = "default")
    public void paymentCaptured(ConsumerRecord<String, String> consumerRecord) {

        PaymentEventData paymentEvent = getPaymentEvent(consumerRecord.value());
        orderService.updateStatusForOrderId(paymentEvent.getOrderId(), OrderStatus.ORDER_PAYMENT_SUCCESS);
        latch.countDown();
    }

    @KafkaListener(topics = "order_payment_failed", groupId = "default")
    public void paymentFailed(ConsumerRecord<String, String> consumerRecord) {
        PaymentEventData paymentEvent = getPaymentEvent(consumerRecord.value());
        orderService.updateStatusForOrderId(paymentEvent.getOrderId(), OrderStatus.ORDER_PAYMENT_FAILED);
        latch.countDown();
    }

    private PaymentEventData getPaymentEvent(String payload) {
        try {
            log.info("received payload='{}'", payload);
            objectMapper.registerModule(new SimpleModule().addDeserializer(SimpleGrantedAuthority.class, new SimpleGrantedAuthorityDeserializer()));
            objectMapper.registerSubtypes(new NamedType(OtelTraceContext.class, "otelTraceContext"));
            KafkaEvent kafkaEvent = objectMapper.readValue(payload, KafkaEvent.class);
            return (PaymentEventData) (kafkaEvent.eventData);
        } catch (JsonProcessingException e) {
            log.error("error processing payload");
            throw new RuntimeException(e);
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
