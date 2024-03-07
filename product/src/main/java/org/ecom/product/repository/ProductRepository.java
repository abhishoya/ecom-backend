package org.ecom.product.repository;

import org.ecom.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByName(String name);
    List<Product> findByCategory(String category);
    List<Product> findByCategoryAndName(String category, String name);
}
