package org.ecom.payment.repository;

import org.ecom.payment.model.*;
import org.springframework.data.mongodb.repository.*;

public interface PaymentRepository extends MongoRepository<PaymentRecord, String>
{
    PaymentRecord findByOrderId(String orderId);
}
