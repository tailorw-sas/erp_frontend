package com.kynsoft.finamer.insis.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroupedRatesKafka {
    private UUID logId;
    private UUID processId;
    private String invoiceDate;
    private String hotelCode;
    private List<ManageRateKafka> manageRateKafkaList;
}
