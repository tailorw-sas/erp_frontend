package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.updateAll;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAllInvoiceCloseOperationCommand implements ICommand {

    private List<UUID> hotels;
    private Status status;
    private LocalDate beginDate;
    private LocalDate endDate;

    public UpdateAllInvoiceCloseOperationCommand(Status status, List<UUID> hotels, LocalDate beginDate, LocalDate endDate) {
        this.status = status;
        this.hotels = hotels;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static UpdateAllInvoiceCloseOperationCommand fromRequest(UpdateAllInvoiceCloseOperationRequest request) {
        return new UpdateAllInvoiceCloseOperationCommand(
                request.getStatus(),
                request.getHotels(),
                request.getBeginDate(),
                request.getEndDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAllInvoiceCloseOperationMessage();
    }
}
