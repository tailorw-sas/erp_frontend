package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.tailorw.tcaInnsist.application.query.objectResponse.RateResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class SycnRateByInvoiceDateMessage implements ICommandMessage {

    private final List<RateResponse> rateResponses;

    public SycnRateByInvoiceDateMessage(List<RateResponse> rateResponses){
        this.rateResponses = rateResponses;
    }
}
