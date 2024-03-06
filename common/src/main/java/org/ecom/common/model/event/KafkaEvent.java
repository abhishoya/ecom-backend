package org.ecom.common.model.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.ecom.common.config.jackson.SimpleGrantedAuthorityDeserializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

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
