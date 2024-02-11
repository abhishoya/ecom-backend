package org.ecom.payment.config;

import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;

import java.util.*;

@Configuration
public class KafkaConfig
{
    @Value("${spring.kafka.bootstrap-server}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin()
    {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1()
    {
        return new NewTopic("inventory_updates", 1, (short) 1);
    }

    @Bean
    public NewTopic topic2()
    {
        return new NewTopic("created_orders", 1, (short) 1);
    }
}
