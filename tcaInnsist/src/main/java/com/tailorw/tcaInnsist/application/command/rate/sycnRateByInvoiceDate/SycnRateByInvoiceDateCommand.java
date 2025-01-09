package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SycnRateByInvoiceDateCommand implements ICommand {

    private UUID processId;
    private String hotel;
    private LocalDate invoiceDate;
    private boolean isFirstGroup;
    private boolean isLastGroup;

    public SycnRateByInvoiceDateCommand(UUID processId,
                                        String hotel,
                                        LocalDate invoiceDate,
                                        boolean isFirstGroup,
                                        boolean isLastGroup){
        this.processId = processId;
        this.hotel = hotel;
        this.invoiceDate = invoiceDate;
        this.isFirstGroup = isFirstGroup;
        this.isLastGroup = isLastGroup;
    }

    @Override
    public ICommandMessage getMessage() {
        return new SycnRateByInvoiceDateMessage();
    }
}
