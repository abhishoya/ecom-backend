package org.ecom.common.model.order;

import lombok.*;
import org.ecom.common.model.order.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.util.*;

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
