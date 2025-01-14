package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SycnRateByInvoiceDateCommand implements ICommand {

    private UUID processId;
    private List<String> hotelList;
    private LocalDate invoiceDate;

    public SycnRateByInvoiceDateCommand(UUID processId,
                                        List<String> hotelList,
                                        LocalDate invoiceDate){
        this.processId = processId;
        this.hotelList = hotelList;
        this.invoiceDate = invoiceDate;
    }

    @Override
    public ICommandMessage getMessage() {
        return new SycnRateByInvoiceDateMessage();
    }
}
