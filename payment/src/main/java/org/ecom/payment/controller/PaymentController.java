package org.ecom.payment.controller;

import lombok.extern.slf4j.Slf4j;
import org.ecom.payment.model.PaymentRecord;
import org.ecom.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@EnableMethodSecurity
public class PaymentController
{
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/get/order/{orderId}")
    public PaymentRecord getPaymentForOrderId(@PathVariable String orderId)
    {
        return paymentService.findByOrderId(orderId);
    }

    @PostMapping("/recordPayment")
    public void recordPayment(@RequestBody String payload)
    {
        paymentService.handlePayment(payload);
    }
}
