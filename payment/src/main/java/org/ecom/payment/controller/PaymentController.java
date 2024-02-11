package org.ecom.payment.controller;

import lombok.extern.slf4j.*;
import org.ecom.payment.model.*;
import org.ecom.payment.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> recordPayment(@RequestBody String payload)
    {
        paymentService.handlePayment(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
