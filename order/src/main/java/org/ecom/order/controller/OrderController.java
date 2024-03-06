package org.ecom.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ecom.common.model.order.Order;
import org.ecom.common.model.user.permission.IsAdmin;
import org.ecom.common.model.user.permission.IsUser;
import org.ecom.order.model.OrderDto;
import org.ecom.order.model.OrderStatusDto;
import org.ecom.order.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
