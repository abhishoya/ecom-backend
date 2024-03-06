package org.ecom.product.service;

import io.micrometer.observation.annotation.Observed;
import jakarta.ws.rs.ProcessingException;
import org.ecom.product.model.Product;
import org.ecom.product.model.ProductDto;
import org.ecom.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
@Observed
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
                    if (product.getQuantity() >= count) {
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
