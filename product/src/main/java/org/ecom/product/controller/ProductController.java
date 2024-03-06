package org.ecom.product.controller;

import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;
import org.ecom.common.model.order.Order;
import org.ecom.common.model.user.permission.IsAdmin;
import org.ecom.common.model.user.permission.IsUser;
import org.ecom.product.model.Product;
import org.ecom.product.model.ProductDto;
import org.ecom.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @IsUser
    public List<Product> decreaseQuantity(@RequestBody Order order)
    {
        Map<String, Integer> mapOfProductIdsAndCount = new HashMap<>();
        order.getItems().forEach(orderItem -> mapOfProductIdsAndCount.put(orderItem.getProductId(), orderItem.getQuantity()));
        return productService.bulkDecrease(mapOfProductIdsAndCount);
    }

    @DeleteMapping("delete/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }
}
