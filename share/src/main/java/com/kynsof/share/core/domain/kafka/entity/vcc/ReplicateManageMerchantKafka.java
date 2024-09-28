package com.kynsof.share.core.domain.kafka.entity.vcc;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplicateManageMerchantKafka {
    private UUID id;
    private String code;
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private String status;
}
