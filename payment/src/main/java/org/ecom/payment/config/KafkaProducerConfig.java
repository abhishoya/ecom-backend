package org.ecom.payment.config;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.ecom.common.model.event.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.*;

import java.util.*;

@Configuration
public class KafkaProducerConfig
{
    @Value("${spring.kafka.bootstrap-server}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, KafkaEvent> producerFactory()
    {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, KafkaEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
