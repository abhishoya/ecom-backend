package org.ecom.common.model.event;

import com.fasterxml.jackson.databind.annotation.*;
import lombok.*;
import org.ecom.common.config.jackson.*;
import org.springframework.security.core.authority.*;

import java.util.*;

@ToString
@Data
@Builder
public class KafkaEvent
{
    @NonNull public String username;
    public @JsonDeserialize(using = SimpleGrantedAuthorityDeserializer.class) List<SimpleGrantedAuthority> authorities;
    public TracingEventData tracingEventData;
    public EventData eventData;
}
