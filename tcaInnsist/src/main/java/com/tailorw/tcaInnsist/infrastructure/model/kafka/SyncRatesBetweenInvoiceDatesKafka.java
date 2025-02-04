package com.tailorw.tcaInnsist.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyncRatesBetweenInvoiceDatesKafka {

    private UUID processId;
    private String hotel;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFirstGroup;
    private boolean isLastGroup;
}
