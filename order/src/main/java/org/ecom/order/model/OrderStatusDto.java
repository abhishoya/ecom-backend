package org.ecom.order.model;

import lombok.*;
import org.ecom.common.model.order.*;

@Data
public class OrderStatusDto
{
    public String id;
    public OrderStatus orderStatus;
}
