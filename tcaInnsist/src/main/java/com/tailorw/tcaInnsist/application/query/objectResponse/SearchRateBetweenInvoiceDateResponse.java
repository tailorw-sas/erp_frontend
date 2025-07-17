package com.tailorw.tcaInnsist.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchRateBetweenInvoiceDateResponse implements IResponse {

    private String hotel;
    private List<RateByInvoiceDateResponse> rateByInvoiceDateResponses;
}
