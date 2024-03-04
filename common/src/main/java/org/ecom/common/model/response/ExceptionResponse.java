package org.ecom.common.model.response;

import io.micrometer.tracing.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;

import java.sql.*;
import java.time.*;


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
