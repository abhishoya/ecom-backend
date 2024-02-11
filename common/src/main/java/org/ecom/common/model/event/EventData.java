package org.ecom.common.model.event;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "orderEvent", value = OrderEventData.class),
        @JsonSubTypes.Type(name = "paymentEvent", value = PaymentEventData.class)
})
public interface EventData
{
}
