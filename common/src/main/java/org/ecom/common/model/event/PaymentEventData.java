package org.ecom.common.model.event;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class PaymentEventData implements EventData
{
    @NonNull public String status;
    @NonNull public String externalPaymentId;
    @NonNull public String orderId;
}
