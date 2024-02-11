package org.ecom.product.service;

import jakarta.ws.rs.*;
import org.ecom.product.model.*;
import org.ecom.product.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Component
public class ProductService
{
    @Autowired
    private ProductRepository repository;

    public List<Product> getAllProducts(@PathVariable Integer pageNumber)
    {
        return repository.findAll(PageRequest.of(pageNumber, 25)).stream().toList();
    }

    public List<Product> getProductsByName(@PathVariable String name)
    {
        return repository.findByName(name);
    }

    public List<Product> getProductsByCategory(@PathVariable String category)
    {
        return repository.findByCategory(category);
    }

    public List<Product> getProductsByCategoryAndName(@PathVariable String category, @PathVariable String name)
    {
        return repository.findByCategoryAndName(category, name);
    }

    public Product getProductById(@PathVariable String id)
    {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product createProduct(@RequestBody Product product)
    {
        return repository.save(product);
    }

    public Product updateProduct(@PathVariable String id, @RequestBody ProductDto updatedProduct)
    {
        return repository.findById(id).map(
                product -> {
                    product.setName(updatedProduct.getName());
                    product.setQuantity(updatedProduct.getQuantity());
                    product.setCategory(updatedProduct.getCategory());
                    return product;
                }).orElseThrow(() -> new RuntimeException(String.format("Product with id %s not found", id)));
    }

    public void deleteProduct(@PathVariable String id) {
        repository.deleteById(id);
    }

    @Transactional
    public List<Product> bulkDecrease(Map<String, Integer> mapOfProductIdsAndCount) {
        List<Product> products = repository.findAllById(mapOfProductIdsAndCount.keySet());
        products.forEach(
                product -> {
                    int count = mapOfProductIdsAndCount.get(product.getId());
                    if (product.getQuantity() > count) {
                        product.setQuantity(product.getQuantity() - count);
                    }
                    else throw new ProcessingException("Product available quantity less than expected");
                }
        );
        List<Product> responseBody = repository.saveAll(products);
        return responseBody;
    }

    @Transactional
    public List<Product> bulkIncrease(Map<String, Integer> mapOfProductIdsAndCount)
    {
        List<Product> products = repository.findAllById(mapOfProductIdsAndCount.keySet());
        products.forEach(
                product -> {
                    int count = mapOfProductIdsAndCount.get(product.getId());
                    product.setQuantity(product.getQuantity() + count);
                }
        );
        return repository.saveAll(products);
    }
}
