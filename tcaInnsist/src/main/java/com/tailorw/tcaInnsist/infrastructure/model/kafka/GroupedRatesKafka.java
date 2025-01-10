package com.tailorw.tcaInnsist.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class GroupedRatesKafka implements Serializable {
    private UUID logId;
    private UUID processId;
    private String invoiceDate;
    private String hotelCode;
    private String reservationCode;
    private String couponNumber;
    private List<ManageRateKafka> rateKafkaList;
    private boolean isFirstGroup;
    private boolean isLastGroup;
}
