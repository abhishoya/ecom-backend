package org.ecom.user.config;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import io.micrometer.tracing.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.*;
import org.ecom.common.model.response.*;
import org.ecom.user.controller.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.*;
import java.text.*;
import java.time.*;
import java.util.*;

@Slf4j
@ControllerAdvice(basePackageClasses = UserController.class)
public class MyControllerAdvice extends ResponseEntityExceptionHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper().setDateFormat(DateFormat.getDateTimeInstance());

    @Autowired
    private Tracer tracer;

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleControllerException(HttpServletRequest request, Throwable ex) throws JsonProcessingException {
        Timestamp t = new Timestamp(Instant.now().toEpochMilli());
        log.error(ex.getMessage());
        return new ResponseEntity<>(ExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message(ex.getMessage()).timestamp(t).requestId(Objects.requireNonNull(tracer.currentTraceContext().context()).traceId()).build(), HttpStatus.BAD_REQUEST);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus status = HttpStatus.resolve(code);
        return (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}

