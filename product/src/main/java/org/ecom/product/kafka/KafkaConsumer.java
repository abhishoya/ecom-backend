package org.ecom.product.kafka;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.*;
import com.fasterxml.jackson.databind.module.*;
import io.micrometer.tracing.*;
import io.micrometer.tracing.brave.bridge.*;
import lombok.extern.slf4j.*;
import org.apache.kafka.clients.consumer.*;
import org.ecom.common.config.jackson.*;
import org.ecom.common.model.event.*;
import org.ecom.product.service.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.kafka.annotation.*;
import org.springframework.security.core.authority.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

@Component
@Slf4j
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private String payload;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @KafkaListener(topics = "failed_orders", groupId = "default")
    public void failedOrder(ConsumerRecord<String, String> consumerRecord) {
        try {
            objectMapper.registerModule(new SimpleModule().addDeserializer(SimpleGrantedAuthority.class, new SimpleGrantedAuthorityDeserializer()));
            objectMapper.registerSubtypes(new NamedType(BraveTraceContext.class, "braveTraceContext"));
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
            payload = objectMapper.writeValueAsString(kafkaEvent);
            LOGGER.info("received payload='{}'", payload);
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

    public String getPayload() {
        return payload;
    }
}
