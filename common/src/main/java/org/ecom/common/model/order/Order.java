package org.ecom.common.model.order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "orders")
@Data
public class Order
{
    @Indexed(unique = true)
    private @Id String id;
    private String username;
    private List<OrderItem> items;
    private OrderStatus status;
}
