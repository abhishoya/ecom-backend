package org.ecom.common.model.log;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Builder
@ToString
public class LogInfo
{
    final String loggingEvent;
    final String message;
    final Timestamp timestamp = Timestamp.from(Instant.now());
}
