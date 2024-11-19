package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicatePaymentResourceTypeKafka {

    private UUID id;
    private String code;
    private String name;
    private boolean invoice;
    private boolean vcc;
}
