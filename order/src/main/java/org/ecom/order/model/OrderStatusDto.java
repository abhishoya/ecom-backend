package org.ecom.order.model;

import lombok.Data;
import org.ecom.common.model.order.OrderStatus;

@Data
public class OrderStatusDto
{
    public String id;
    public OrderStatus orderStatus;
}
