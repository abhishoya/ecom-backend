package org.ecom.order.repository;

import org.ecom.common.model.order.*;
import org.springframework.data.mongodb.repository.*;

import java.util.*;

public interface OrderRepository extends MongoRepository<Order, String>
{
    List<Order> getOrdersByUsername(String username);
}
