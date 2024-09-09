package com.kynsof.share.core.domain.kafka.entity.vcc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageMerchantConfigKafka {
    private UUID id;
}
