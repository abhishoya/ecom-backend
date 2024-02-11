package org.ecom.common.model.log;

import com.fasterxml.jackson.databind.annotation.*;
import lombok.*;

import java.io.*;
import java.sql.*;
import java.time.*;

@Getter
@Builder
@ToString
public class LogInfo
{
    final String loggingEvent;
    final String message;
    final Timestamp timestamp = Timestamp.from(Instant.now());
}
