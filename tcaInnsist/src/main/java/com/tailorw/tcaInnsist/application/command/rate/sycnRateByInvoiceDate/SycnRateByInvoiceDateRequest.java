package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SycnRateByInvoiceDateRequest {
    private UUID processId;
    private String hotel;
    private String invoiceDate;
}
