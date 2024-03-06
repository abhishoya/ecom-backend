package org.ecom.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Router
{
    @Autowired
    private AuthPreFilter authFilter;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder)
    {
        return builder.routes().route(
                "auth-service-route", p -> p.path("/api/auth/**").filters(f -> f.filter(authFilter.apply(new AuthPreFilter.Config()))).uri("http://localhost:8081/")
        ).route(
                "user-service-route", p -> p.path("/api/user/**").filters(f -> f.filter(authFilter.apply(new AuthPreFilter.Config()))).uri("http://localhost:8082/")
        ).route(
                "product-service-route", p -> p.path("/api/product/**").filters(f -> f.filter(authFilter.apply(new AuthPreFilter.Config()))).uri("http://localhost:8083/")
        ).route(
                "order-service-route", p -> p.path("/api/order/**").filters(f -> f.filter(authFilter.apply(new AuthPreFilter.Config()))).uri("http://localhost:8084/")
        ).route(
                "payment-service-route", p -> p.path("/api/payment/**").filters(f -> f.filter(authFilter.apply(new AuthPreFilter.Config()))).uri("http://localhost:8085/")
        ).build();
    }
}
