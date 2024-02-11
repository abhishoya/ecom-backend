package org.ecom.common.model.event;

import lombok.*;

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
