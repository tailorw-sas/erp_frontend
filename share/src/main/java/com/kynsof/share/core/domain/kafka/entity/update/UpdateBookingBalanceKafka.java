package com.kynsof.share.core.domain.kafka.entity.update;

import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateBookingBalanceKafka {
    private UUID id;
    private Double amountBalance;//dueAmount
    private ReplicatePaymentKafka paymentKafka;
}
