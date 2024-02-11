package org.ecom.common.model.response;

import lombok.*;
import org.springframework.http.*;

import java.sql.*;


@Setter
@Getter
@Builder
public class ExceptionResponse
{
    final HttpStatus status;
    final String message;
    final Timestamp timestamp;
    final String url;
    final String errorId;
}
