package org.ecom.gateway;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import io.micrometer.observation.annotation.*;
import io.micrometer.tracing.*;
import io.micrometer.tracing.annotation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.ecom.common.model.response.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.cloud.gateway.filter.factory.*;
import org.springframework.context.annotation.*;
import org.springframework.core.io.buffer.*;
import org.springframework.http.*;
import org.springframework.http.server.reactive.*;
import org.springframework.web.reactive.function.client.*;
import org.springframework.web.server.*;
import reactor.core.publisher.*;

import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Slf4j
@Configuration
public class AuthPreFilter extends AbstractGatewayFilterFactory<AuthPreFilter.Config>
{
    List<String> excludedUrls = List.of(
            "/api/(.*)/swagger/(.*)",
            "/api/auth/login",
            "/api/user/signup",
            "/api/product/get/(.*)",
            "/api/payment/recordPayment"
    );

    List<String> privateUrls = List.of(
            "/api/(.*)/private/(.*)"
    );

    @Autowired
    private ObjectMapper objectMapper;

    private final WebClient.Builder webClientBuilder;

    public AuthPreFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder=webClientBuilder;
    }

    @Override
    @NewSpan
    @SpanName("filter")
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            log.info("**************************************************************************");
            log.info("URL is - " + request.getURI().getPath());
            String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if(isPrivate.test(request))
            {
                return onError(exchange, String.valueOf(HttpStatus.NOT_FOUND), "", "", HttpStatus.NOT_FOUND);
            }

            if(isSecured.test(request))
            {
                log.info("URL is secured");
                return webClientBuilder.build().get()
                        .uri("http://localhost:8081/api/auth/validateToken")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .retrieve().bodyToMono(ConnValidationResponse.class)
                        .map(response -> {
                            exchange.getRequest().mutate().header("username", response.getUsername());
                            exchange.getRequest().mutate().header("authorities", response.getAuthorities().stream().map(m -> (String) m.get("authority")).collect(Collectors.joining(",")));
                            exchange.getRequest().mutate().header("auth-token", response.getToken());
                            return exchange;
                        }).flatMap(chain::filter).onErrorResume(error -> {
                            log.info("Error Happened");
                            HttpStatusCode errorCode = null;
                            String errorMsg = "";
                            if (error instanceof WebClientResponseException webCLientException) {
                                errorCode = webCLientException.getStatusCode();
                                errorMsg = webCLientException.getStatusText();
                            } else {
                                errorCode = HttpStatus.BAD_GATEWAY;
                                errorMsg = HttpStatus.BAD_GATEWAY.getReasonPhrase();
                            }

                            return onError(exchange, String.valueOf(errorCode.value()) ,errorMsg, "JWT Authentication Failed", HttpStatus.valueOf(errorCode.value()));
                        });
            }
            else
            {
                return chain.filter(exchange);
            }
        };
    }

    public Predicate<org.springframework.http.server.reactive.ServerHttpRequest> isSecured = request -> excludedUrls.stream().noneMatch(uri -> request.getURI().getPath().matches(uri));

    public Predicate<org.springframework.http.server.reactive.ServerHttpRequest> isPrivate = request -> privateUrls.stream().anyMatch(uri -> request.getURI().getPath().matches(uri));

    private Mono<Void> onError(ServerWebExchange exchange, String errCode, String err, String errDetails, HttpStatus httpStatus) {
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        org.springframework.http.server.reactive.ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        try {
            response.getHeaders().add("Content-Type", "application/json");
            ExceptionResponse data = ExceptionResponse.builder().status(HttpStatus.valueOf(Integer.parseInt(errCode))).message(err).timestamp(new Timestamp(Instant.now().toEpochMilli())).build();
            byte[] byteData = objectMapper.writeValueAsBytes(data);
            return response.writeWith(Mono.just(byteData).map(t -> dataBufferFactory.wrap(t)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response.setComplete();
    }

    @NoArgsConstructor
    public static class Config
    {
    }
}
