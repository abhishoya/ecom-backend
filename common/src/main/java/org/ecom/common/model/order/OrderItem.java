package org.ecom.common.model.order;

import lombok.*;

@Data
public class OrderItem
{
    private String productId;
    private Integer quantity;
}
