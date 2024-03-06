package org.ecom.common.model.order;

import lombok.Data;

@Data
public class OrderItem
{
    private String productId;
    private Integer quantity;
}
