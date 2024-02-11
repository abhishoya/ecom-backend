package org.ecom.order.service;

import io.micrometer.tracing.*;
import lombok.extern.slf4j.*;
import org.ecom.common.model.event.*;
import org.ecom.common.model.order.*;
import org.ecom.common.model.user.*;
import org.ecom.order.kafka.*;
import org.ecom.order.repository.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

import java.net.*;
import java.util.*;
import java.util.stream.*;

@Slf4j
@Component
public class OrderService
{
    @Autowired
    private OrderRepository repository;

    private final String productServiceUri = "http://localhost:8083/api/product";

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ModelMapper modelMapper;

    public List<Order> getAllOrders()
    {
        return repository.findAll();
    }

    public List<Order> getOrderByUsername(String username)
    {
        return repository.getOrdersByUsername(username);
    }

    public Order createOrder(Order order)
    {
        updateInventory(order);
        order.setStatus(OrderStatus.ORDER_CREATE_SUCCESS);
        return repository.save(order);
    }

    public void updateInventory(Order order)
    {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<SimpleGrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority instanceof SimpleGrantedAuthority)
                .map(grantedAuthority -> (SimpleGrantedAuthority) grantedAuthority)
                .toList();
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.put("username", Collections.singletonList(username));
        headers.put("authorities", Collections.singletonList(authorities.stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.joining(","))));
        headers.put("auth-token", Collections.singletonList(token));
        RequestEntity<Order> req = new RequestEntity<>(order, headers, HttpMethod.PUT, URI.create(productServiceUri + "/private/bulk/decreaseQuantity"));
        new RestTemplate().exchange(req, Object.class);
    }

    public Order cancelOrder(@PathVariable String id) {
        Order order = repository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format("Order with id %s not found", id)));
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        if(order.getUsername().equals(username) || isAdmin)
        {
            return updateOrderStatus(order, OrderStatus.ORDER_CANCELLED);
        }
        else
        {
            throw new IllegalStateException(String.format("Order username %s doesn't match authenticated username %s", order.getUsername(), username));
        }
    }

    public void updateStatusForOrderId(String id, OrderStatus orderStatus) {
        Optional<Order> order = repository.findById(id);
        if(order.isPresent())
        {
            log.info("updateOrder for order id {} orderStatus {}", id, orderStatus);
            updateOrderStatus(order.get(), orderStatus);
        }
        else
        {
            log.warn("updateOrder called upon invalid order id {} orderStatus {}", id, orderStatus);
        }
    }

    private Order updateOrderStatus(Order order, OrderStatus orderStatus) {
        if(!order.getStatus().equals(orderStatus)) // only process once
        {
            if(orderStatus.isFailed())
            {
                kafkaProducer.sendMessage(
                        orderStatus.name().toLowerCase(),
                        KafkaEvent.builder()
                                .tracingEventData(TracingEventData.from(Objects.requireNonNull(tracer.currentTraceContext().context())))
                                .eventData(OrderEventData.from(order))
                                .username("order-admin")
                                .authorities(UserRole.ADMIN.getAuthorities().stream().toList())
                                .build()
                );
            }
            if(orderStatus.equals(OrderStatus.ORDER_CANCELLED))
            {

            }
            order.setStatus(orderStatus);
            repository.save(order);
        }
        return order;
    }
}
