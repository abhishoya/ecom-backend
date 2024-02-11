package org.ecom.product.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;

@Document(collection = "products")
@Data
public class Product {
    @Indexed(unique = true)
    private @Id String id;

    private String name;

    private String category;

    private Integer quantity;

    private List<String> mediaUrls;
}
