package org.ecom.common.model.event;

import lombok.Data;
import org.ecom.common.model.order.Order;
import org.ecom.common.model.order.OrderItem;

import java.util.List;

@Data
public class OrderEventData implements EventData {
    private String id;
    private List<OrderItem> items;

    public static OrderEventData from(Order order)
    {
        OrderEventData orderEventData = new OrderEventData();
        orderEventData.id = order.getId();
        orderEventData.items = order.getItems();
        return orderEventData;
    }
}
