package org.ecom.payment.repository;

import org.ecom.payment.model.PaymentRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<PaymentRecord, String>
{
    PaymentRecord findByOrderId(String orderId);
}
