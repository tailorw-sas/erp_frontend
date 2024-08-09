package com.kynsoft.finamer.invoicing.application.command.checkDatesInCloseOperation.checkDates;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CheckDatesInCloseOperationCommandHandler implements ICommandHandler<CheckDatesInCloseOperationCommand> {

    private final IInvoiceCloseOperationService closeOperationService;

    public CheckDatesInCloseOperationCommandHandler(IInvoiceCloseOperationService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(CheckDatesInCloseOperationCommand command) {
        for(LocalDate date : command.getDates()){
            RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(this.closeOperationService, date, command.getHotelId()));
        }
    }
}
