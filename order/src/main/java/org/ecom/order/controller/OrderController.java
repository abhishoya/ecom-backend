package org.ecom.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import io.micrometer.tracing.*;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import lombok.extern.slf4j.*;
import org.ecom.common.model.order.*;
import org.ecom.common.model.response.*;
import org.ecom.common.model.user.permission.*;
import org.ecom.order.kafka.KafkaProducer;
import org.ecom.order.model.*;
import org.ecom.order.service.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

import java.sql.*;
import java.time.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/order")
@EnableMethodSecurity
public class OrderController
{
    @Autowired
    private OrderService orderService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("list")
    @IsAdmin
    public List<Order> getAllOrders()
    {
        return orderService.getAllOrders();
    }

    @GetMapping("get")
    @IsUser
    public List<Order> getOrdersForUser()
    {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.getOrderByUsername(username);
    }

    @PostMapping("create")
    @IsUser
    public Order createOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderDto orderDto) throws JsonProcessingException {
        Order order = modelMapper.map(orderDto, Order.class);
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        order.setUsername(username);
        return orderService.createOrder(order);
    }

    @PutMapping("cancel/{id}")
    @IsUser
    public Order cancelOrder(@PathVariable String id) {
        return orderService.cancelOrder(id);
    }

    @PutMapping("updateStatus")
    @IsAdmin
    public Order updateOrderStatus(@RequestBody OrderStatusDto order)
    {
        return orderService.updateOrder(order);
    }
}
