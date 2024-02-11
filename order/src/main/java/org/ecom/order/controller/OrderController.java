package org.ecom.order.controller;

import com.fasterxml.jackson.databind.*;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaProducer kafkaProducer;

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
    public ResponseEntity<Object> createOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody OrderDto orderDto)
    {
        try
        {
            Order order = modelMapper.map(orderDto, Order.class);
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            order.setUsername(username);
            return new ResponseEntity<>(orderService.createOrder(order), HttpStatus.OK);
        }
        catch (HttpClientErrorException e)
        {
            try
            {
                ExceptionResponse resp = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionResponse.class);
                return new ResponseEntity<>(resp, resp.getStatus());
            }
            catch (Exception ex)
            {
                return new ResponseEntity<>(ExceptionResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("Could not create order: " + e.getMessage()).timestamp(Timestamp.from(Instant.now())).build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PutMapping("cancel/{id}")
    @IsUser
    public ResponseEntity<Object> cancelOrder(@PathVariable String id) {
        try{
            return new ResponseEntity<>(orderService.cancelOrder(id), HttpStatus.OK);
        }
        catch (IllegalArgumentException exception)
        {
            log.error(exception.getMessage());
            return new ResponseEntity<>(ExceptionResponse.builder().message(String.format("Order with id %s not found", id)).timestamp(Timestamp.from(Instant.now())).status(HttpStatus.BAD_REQUEST).build(), HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            return new ResponseEntity<>(ExceptionResponse.builder().message("Internal Server Error").timestamp(Timestamp.from(Instant.now())).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
