package org.ecom.payment.kafka;

import org.ecom.common.model.event.KafkaEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
