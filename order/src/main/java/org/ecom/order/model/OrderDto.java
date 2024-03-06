package org.ecom.order.model;

import lombok.Data;
import org.ecom.common.model.order.OrderItem;

import java.util.List;

@Data
public class OrderDto {
    private List<OrderItem> items;
}
