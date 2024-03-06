package org.ecom.payment.service;

import com.fasterxml.jackson.databind.*;
import io.micrometer.tracing.*;
import org.ecom.common.model.event.*;
import org.ecom.common.model.user.*;
import org.ecom.payment.kafka.*;
import org.ecom.payment.model.*;
import org.ecom.payment.repository.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class PaymentService
{
    @Autowired
    private PaymentRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private Tracer tracer;

    public void handlePayment(String payload)
    {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String event = jsonNode.get("event").textValue();
            JsonNode paymentEntity = jsonNode.get("payload").get("payment").get("entity");
            String externalPaymentId = paymentEntity.get("id").textValue();
            String orderId = paymentEntity.get("order_id").textValue();
            String currency = paymentEntity.get("currency").textValue();
            Integer amount = paymentEntity.get("amount").intValue();
            PaymentRecord paymentRecord = new PaymentRecord(event.split("\\.")[1], externalPaymentId, orderId, amount, currency);
            repository.save(paymentRecord);
            if (Objects.equals(event, "payment.captured")) {
                kafkaProducer.sendMessage("order_payment_success",
                        KafkaEvent.builder()
                                .tracingEventData(TracingEventData.from(tracer.currentSpan().context()))
                                .username("payment-admin")
                                .authorities(UserRole.ADMIN.getAuthorities().stream().toList())
                                .eventData(modelMapper.map(paymentRecord, PaymentEventData.class))
                                .build()
                );
            } else {
                kafkaProducer.sendMessage("order_payment_failed",
                        KafkaEvent.builder()
                                .tracingEventData(TracingEventData.from(tracer.currentSpan().context()))
                                .username("payment-admin")
                                .authorities(UserRole.ADMIN.getAuthorities().stream().toList())
                                .eventData(modelMapper.map(paymentRecord, PaymentEventData.class))
                                .build()
                );
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentRecord findByOrderId(String orderId) {
        return repository.findByOrderId(orderId);
    }
}
