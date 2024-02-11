package org.ecom.payment.kafka;

import org.ecom.common.model.event.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

@Component
public class KafkaProducer
{
    @Autowired
    private KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public void sendMessage(String topic, KafkaEvent event)
    {
        kafkaTemplate.send(topic, event);
    }
}
