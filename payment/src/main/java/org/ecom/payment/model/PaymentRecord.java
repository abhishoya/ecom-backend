package org.ecom.payment.model;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.*;

import java.time.*;

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
