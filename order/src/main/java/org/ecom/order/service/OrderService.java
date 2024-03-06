package org.ecom.order.service;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.ecom.common.model.event.KafkaEvent;
import org.ecom.common.model.event.OrderEventData;
import org.ecom.common.model.event.TracingEventData;
import org.ecom.common.model.order.Order;
import org.ecom.common.model.order.OrderStatus;
import org.ecom.common.model.user.UserRole;
import org.ecom.order.kafka.KafkaProducer;
import org.ecom.order.model.OrderStatusDto;
import org.ecom.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        order.setStatus(OrderStatus.ORDER_CREATED);
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

    public Order cancelOrder(String id) {
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

    public Order updateStatusForOrderId(String id, OrderStatus orderStatus) {
        Optional<Order> order = repository.findById(id);
        if(order.isPresent())
        {
            log.info("updateOrder for order id {} orderStatus {}", id, orderStatus);
            return updateOrderStatus(order.get(), orderStatus);
        }
        else
        {
            log.warn("updateOrder called upon invalid order id {} orderStatus {}", id, orderStatus);
            throw new IllegalArgumentException(String.format("updateOrder called for invalid order id %s", id));
        }
    }

    public Order updateOrder(OrderStatusDto orderStatusDto) throws IllegalArgumentException {
        return updateStatusForOrderId(orderStatusDto.getId(), orderStatusDto.getOrderStatus());
    }

    private Order updateOrderStatus(Order order, OrderStatus orderStatus) {
        if(!order.getStatus().equals(orderStatus)) // only process once
        {
            if(orderStatus.isFailed())
            {
                kafkaProducer.sendMessage(
                        "order_revert_inventory",
                        KafkaEvent.builder()
                                .tracingEventData(TracingEventData.from(Objects.requireNonNull(tracer.currentTraceContext().context())))
                                .eventData(OrderEventData.from(order))
                                .username("order-admin")
                                .authorities(UserRole.ADMIN.getAuthorities().stream().toList())
                                .build()
                );
            }
            order.setStatus(orderStatus);
            repository.save(order);
        }
        return order;
    }
}
