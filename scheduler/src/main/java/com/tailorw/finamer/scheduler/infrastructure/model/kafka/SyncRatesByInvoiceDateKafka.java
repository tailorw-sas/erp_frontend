package com.tailorw.finamer.scheduler.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SyncRatesByInvoiceDateKafka {
    private UUID processId;
    private List<String> hotels;
    private String invoiceDate;
}
