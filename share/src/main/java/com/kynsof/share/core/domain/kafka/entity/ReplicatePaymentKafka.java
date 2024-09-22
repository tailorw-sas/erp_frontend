package com.kynsof.share.core.domain.kafka.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReplicatePaymentKafka implements Serializable {
    private UUID id;
    private Long paymentId;
    private ReplicatePaymentDetailsKafka details;
}
