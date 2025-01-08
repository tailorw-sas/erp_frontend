package com.tailorw.tcaInnsist.application.command.rate.syncRateBetweenInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SyncRateBetweenInvoiceDateCommand implements ICommand {

    private UUID processId;
    private String hotel;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isFirstGroup;
    private boolean isLastGroup;

    public SyncRateBetweenInvoiceDateCommand(UUID processId, String hotel, LocalDate startDate, LocalDate endDate, boolean isFirstGroup, boolean isLastGroup){
        this.processId = processId;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isFirstGroup = isFirstGroup;
        this.isLastGroup = isLastGroup;
    }

    @Override
    public ICommandMessage getMessage() {
        return new SycnRateByInvoiceDateMessage();
    }
}
