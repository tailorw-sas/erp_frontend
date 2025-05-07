package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplicateBookingKafka {
    private UUID id;
    private Double amountBalance;//dueAmount
    private boolean deposit;
    private OffsetDateTime timestamp;
}
