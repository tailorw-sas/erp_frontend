package com.tailorw.tcaInnsist.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class GroupedRatesKafka {
    private UUID logId;
    private UUID processId;
    private String invoiceDate;
    private String hotelCode;
    private List<ManageRateKafka> manageRateKafkaList;
}
