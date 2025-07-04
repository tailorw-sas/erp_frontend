package com.kynsoft.finamer.payment.application.command.payment.expense;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.application.services.payment.credit.CreatePaymentFromCreditService;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateExpenseFromCreditHandler implements ICommandHandler<CreateExpenseFromCreditCommand> {

    private final IManageInvoiceService manageInvoiceService;
    private final CreatePaymentFromCreditService createPaymentFromCreditService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    public CreateExpenseFromCreditHandler(IManageInvoiceService manageInvoiceService,
                                          CreatePaymentFromCreditService createPaymentFromCreditService,
                                          ReplicateBookingBalanceService replicateBookingBalanceService){
        this.manageInvoiceService = manageInvoiceService;
        this.createPaymentFromCreditService = createPaymentFromCreditService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void handle(CreateExpenseFromCreditCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getInvoice(), "Invoice ID", "The invoice ID param must not be null"));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getClient(), "Client ID", "The client ID param must not be null"));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAgency(), "The Agency ID", "The agency ID param must not be null"));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "Hotel ID", "The hotel ID param must not be null"));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "Employee ID", "The employee ID param must not be null"));

        ManageInvoiceDto invoiceDto = this.getInvoice(command.getInvoice());
        List<ManageBookingDto> bookingsToUpdate = new ArrayList<>();

        this.createPaymentFromCreditService.create(command.getHotel(),
                command.getClient(),
                command.getAgency(),
                command.getEmployee(),
                invoiceDto,
                command.getAttachments(),
                bookingsToUpdate);

        List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = ReplicateBookingBalanceHelper.from(bookingsToUpdate, false);
        this.replicateBookingBalanceService.replicateBooking(replicateBookingBalanceHelpers);
    }

    private ManageInvoiceDto getInvoice(UUID id){
        return this.manageInvoiceService.findById(id);
    }
}
