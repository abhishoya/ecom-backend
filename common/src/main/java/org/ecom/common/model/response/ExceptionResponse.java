package org.ecom.common.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.Instant;


@Setter
@Getter
@Builder
public class ExceptionResponse
{
    final HttpStatus status;
    final String message;
    @Builder.Default final Timestamp timestamp = Timestamp.from(Instant.now());
    final String requestId;
}
