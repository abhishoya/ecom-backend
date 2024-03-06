package org.ecom.product.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
