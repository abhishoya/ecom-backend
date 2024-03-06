package org.ecom.common.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.ecom.common.model.response.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;

import java.util.Objects;

@Slf4j
@Component
@ControllerAdvice(basePackages = "org.ecom")
public class ExceptionControllerAdvice
{
    @Autowired
    private Tracer tracer;

    @Autowired
    private ObjectMapper objectMapper;

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(HttpServletRequest request, Throwable ex) throws JsonProcessingException {
        log.error(ex.getMessage());
        if (ex instanceof RestClientResponseException)
        {
            return ResponseEntity.internalServerError().body(objectMapper.readValue(((RestClientResponseException) ex).getResponseBodyAsString(), ExceptionResponse.class));
        }
        return ResponseEntity.internalServerError().body(
                ExceptionResponse.builder()
                        .message("Something went wrong")
                        .requestId(Objects.requireNonNull(tracer.currentTraceContext().context()).traceId())
                        .build()
        );
    }
}
