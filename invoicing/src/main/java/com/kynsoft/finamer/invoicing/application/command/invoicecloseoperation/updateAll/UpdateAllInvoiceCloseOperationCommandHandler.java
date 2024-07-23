package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.updateAll;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.rules.closeOperation.CheckBeginDateAndEndDateRule;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdateAllInvoiceCloseOperationCommandHandler implements ICommandHandler<UpdateAllInvoiceCloseOperationCommand> {

    private final IInvoiceCloseOperationService closeOperationService;

    public UpdateAllInvoiceCloseOperationCommandHandler(IInvoiceCloseOperationService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(UpdateAllInvoiceCloseOperationCommand command) {

        RulesChecker.checkRule(new CheckBeginDateAndEndDateRule(command.getBeginDate(), command.getEndDate()));
        List<InvoiceCloseOperationDto> closeOperationDtos = this.closeOperationService.findByHotelIds(command.getHotels());
        List<InvoiceCloseOperationDto> update = new ArrayList<>();

        for (InvoiceCloseOperationDto closeOperationDto : closeOperationDtos) {
            updatePaymentCloseOperation(closeOperationDto, command);
            update.add(closeOperationDto);
        }

        this.closeOperationService.updateAll(update);

    }

    private void updatePaymentCloseOperation(InvoiceCloseOperationDto updateC, UpdateAllInvoiceCloseOperationCommand command) {

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateStatus(updateC::setStatus, command.getStatus(), updateC.getStatus(), update::setUpdate);
        this.updateLocalDate(updateC::setBeginDate, command.getBeginDate(), updateC.getBeginDate(), update::setUpdate);
        this.updateLocalDate(updateC::setEndDate, command.getEndDate(), updateC.getEndDate(), update::setUpdate);

    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

    private void updateLocalDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
