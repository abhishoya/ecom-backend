package org.ecom.product.controller;

import io.micrometer.tracing.annotation.*;
import lombok.extern.slf4j.*;
import org.ecom.common.model.order.Order;
import org.ecom.common.model.response.*;
import org.ecom.common.model.user.permission.*;
import org.ecom.product.model.*;
import org.ecom.product.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/product")
@EnableMethodSecurity
public class ProductController
{
    @Autowired
    private ProductService productService;

    @GetMapping("/get/all/{pageNumber}")
    public List<Product> getAllProducts(@PathVariable Integer pageNumber)
    {
        return productService.getAllProducts(pageNumber);
    }

    @GetMapping("/get/name/{name}")
    public List<Product> getProductsByName(@PathVariable String name)
    {
        return productService.getProductsByName(name);
    }

    @GetMapping("/get/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category)
    {
        return productService.getProductsByCategory(category);
    }

    @GetMapping("/get/category/{category}/name/{name}")
    public List<Product> getProductsByCategoryAndName(@PathVariable String category, @PathVariable String name)
    {
        return productService.getProductsByCategoryAndName(category, name);
    }

    @GetMapping("/get/{id}")
    public Product getProductById(@PathVariable String id)
    {
        return productService.getProductById(id);
    }

    @PostMapping("/create")
    public Product createProduct(@RequestBody Product product)
    {
        return productService.createProduct(product);
    }

    @PutMapping("/update/{id}")
    @IsAdmin
    public Product updateProduct(@PathVariable String id, @RequestBody ProductDto updatedProduct)
    {
        return productService.updateProduct(id, updatedProduct);
    }

    @PutMapping("/private/bulk/decreaseQuantity")
    @NewSpan("process-order")
    @Transactional
    @IsUser
    public ResponseEntity<Object> decreaseQuantity(@RequestBody Order order)
    {
        try
        {
            Map<String, Integer> mapOfProductIdsAndCount = new HashMap<>();
            order.getItems().forEach(orderItem -> mapOfProductIdsAndCount.put(orderItem.getProductId(), orderItem.getQuantity()));
            List<Product> responseBody = productService.bulkDecrease(mapOfProductIdsAndCount);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            return new ResponseEntity<>(ExceptionResponse.builder().message(e.getMessage()).status(HttpStatus.BAD_REQUEST).timestamp(Timestamp.from(Instant.now())).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("delete/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
