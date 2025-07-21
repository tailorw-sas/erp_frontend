package com.tailorw.tcaInnsist.application.query.rate.searchRateBetweenInvoiceDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchRateBetweenInvoiceDateRequest {
    private UUID processId;
    private String hotel;
    private String fromInvoiceDate;
    private String toInvoiceDate;
}
