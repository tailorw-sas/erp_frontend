package com.tailorw.tcaInnsist.application.query.objectResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RateByInvoiceDateResponse {

    private String invoiceDate;
    private List<RateResponse> rateResponses;
}
