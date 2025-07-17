package com.kynsoft.finamer.insis.infrastructure.services.http.entities.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SycnRateByInvoiceDateRequest {

    private UUID processId;
    private String hotel;
    private String invoiceDate;
}
