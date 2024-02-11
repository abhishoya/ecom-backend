package org.ecom.order.model;

import lombok.*;
import org.ecom.common.model.order.*;

import java.util.*;

@Data
public class OrderDto {
    private List<OrderItem> items;
}
