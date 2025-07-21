package com.kynsoft.finamer.insis.infrastructure.services.http.entities.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyncRateByInvoiceDateMessage {

    private List<RateResponse> rateResponses;
}
