package com.kynsoft.finamer.insis.infrastructure.services.http.entities.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SearchRateBetweenInvoiceDateRequest {
    private UUID processId;
    private String hotel;
    private String fromInvoiceDate;
    private String toInvoiceDate;
}
