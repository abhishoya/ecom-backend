package org.ecom.product.repository;

import org.ecom.product.model.*;
import org.springframework.data.mongodb.repository.*;

import java.util.*;

public interface ProductRepository extends MongoRepository<Product, String> {
    public List<Product> findByName(String name);
    public List<Product> findByCategory(String category);
    public List<Product> findByCategoryAndName(String category, String name);
}
