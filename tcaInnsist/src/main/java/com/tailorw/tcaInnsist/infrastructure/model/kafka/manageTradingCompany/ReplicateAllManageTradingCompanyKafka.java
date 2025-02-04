package com.tailorw.tcaInnsist.infrastructure.model.kafka.manageTradingCompany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateAllManageTradingCompanyKafka {
    private List<ReplicateManageTradingCompanyKafka> replicateManageTradingCompanyKafkaList;
}
