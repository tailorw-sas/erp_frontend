package com.kynsoft.finamer.insis.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplicateManageTradingCompanyKafka {
    private UUID id;
    private String code;
    private String name;
    private UUID connectionId;
}
