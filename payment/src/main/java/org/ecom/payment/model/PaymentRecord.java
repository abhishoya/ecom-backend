package org.ecom.payment.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "payments")
@Data
@RequiredArgsConstructor
public class PaymentRecord
{
    @Indexed(unique = true)
    private @Id String id;
    @NonNull public String status;
    @NonNull public String externalPaymentId;
    @NonNull public String orderId;
    @NonNull public Integer amount;
    @NonNull public String currency;
}
