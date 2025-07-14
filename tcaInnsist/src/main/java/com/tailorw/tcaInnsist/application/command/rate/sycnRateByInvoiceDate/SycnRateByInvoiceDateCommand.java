package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import com.kynsof.share.utils.DateConvert;
import com.tailorw.tcaInnsist.application.query.objectResponse.RateResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class SycnRateByInvoiceDateCommand implements ICommand {

    private UUID processId;
    private List<String> hotelList;
    private LocalDate invoiceDate;
    private Boolean manual;
    private List<RateResponse> rateResponses;

    public SycnRateByInvoiceDateCommand(UUID processId,
                                        List<String> hotelList,
                                        LocalDate invoiceDate,
                                        Boolean manual){
        this.processId = processId;
        this.hotelList = hotelList;
        this.invoiceDate = invoiceDate;
        this.manual = manual;
    }

    public static SycnRateByInvoiceDateCommand fromRequest(SycnRateByInvoiceDateRequest request){
        return new SycnRateByInvoiceDateCommand(
                request.getProcessId(),
                List.of(request.getHotel()),
                DateConvert.convertStringToLocalDate(request.getInvoiceDate(), DateConvert.getIsoLocalDateFormatter()),
                true
        );
    }
    @Override
    public ICommandMessage getMessage() {
        return new SycnRateByInvoiceDateMessage(this.rateResponses);
    }
}
