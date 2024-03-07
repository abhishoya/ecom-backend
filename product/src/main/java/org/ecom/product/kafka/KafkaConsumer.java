package org.ecom.product.kafka;

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
import org.ecom.common.model.event.OrderEventData;
import org.ecom.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
@Slf4j
public class  KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    private Tracer tracer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @KafkaListener(topics = "order_revert_inventory", groupId = "default")
    public void failedOrder(ConsumerRecord<String, String> consumerRecord) {
        try {
            LOGGER.info("received payload='{}'", consumerRecord.value());
            objectMapper.registerModule(new SimpleModule().addDeserializer(SimpleGrantedAuthority.class, new SimpleGrantedAuthorityDeserializer()));
            objectMapper.registerSubtypes(new NamedType(OtelTraceContext.class, "otelTraceContext"));
            KafkaEvent kafkaEvent = objectMapper.readValue(consumerRecord.value(), KafkaEvent.class);
            OrderEventData orderEventData = (OrderEventData) (kafkaEvent.eventData);
            Span span = tracer.spanBuilder()
                    .name("process_failed_order")
                    .tag("order_id", orderEventData.getId())
                    .tag("username", kafkaEvent.getUsername())
                    .tag("authorities", kafkaEvent.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                    .setParent(tracer.traceContextBuilder().spanId(kafkaEvent.tracingEventData.getSpanId()).parentId(kafkaEvent.tracingEventData.getParentId()).traceId(kafkaEvent.tracingEventData.getTraceId()).build())
                    .start();
            tracer.currentTraceContext().newScope(span.context());
            log.info(tracer.currentSpan().toString());
            Map<String, Integer> entries = new HashMap<>();
            orderEventData.getItems().forEach(orderItem -> entries.put(orderItem.getProductId(), orderItem.getQuantity()));
            productService.bulkIncrease(entries);
            latch.countDown();
        }
        catch (JsonProcessingException e)
        {
            log.error("error processing payload");
        } finally {
            tracer.currentSpan().end();
        }
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
