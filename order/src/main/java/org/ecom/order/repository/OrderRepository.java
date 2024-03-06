package org.ecom.order.repository;

import org.ecom.common.model.order.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String>
{
    List<Order> getOrdersByUsername(String username);
}
