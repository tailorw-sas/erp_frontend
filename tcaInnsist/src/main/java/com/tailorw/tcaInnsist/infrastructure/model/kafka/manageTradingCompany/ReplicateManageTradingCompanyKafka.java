package com.tailorw.tcaInnsist.infrastructure.model.kafka.manageTradingCompany;

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
