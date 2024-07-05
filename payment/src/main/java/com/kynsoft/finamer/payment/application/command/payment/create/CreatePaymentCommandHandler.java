package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckIfDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentCommandHandler implements ICommandHandler<CreatePaymentCommand> {

    private final IManagePaymentSourceService sourceService;
    private final IManagePaymentStatusService statusService;
    private final IManageClientService clientService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageBankAccountService bankAccountService;
    private final IManagePaymentAttachmentStatusService attachmentStatusService;
    private final IPaymentService paymentService;

    public CreatePaymentCommandHandler(IManagePaymentSourceService sourceService,
                                       IManagePaymentStatusService statusService,
                                       IManageClientService clientService,
                                       IManageAgencyService agencyService,
                                       IManageBankAccountService bankAccountService,
                                       IManagePaymentAttachmentStatusService attachmentStatusService,
                                       IPaymentService paymentService,
                                       IManageHotelService hotelService) {
        this.sourceService = sourceService;
        this.statusService = statusService;
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.bankAccountService = bankAccountService;
        this.attachmentStatusService = attachmentStatusService;
        this.paymentService = paymentService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreatePaymentCommand command) {

        RulesChecker.checkRule(new CheckIfDateIsBeforeCurrentDateRule(command.getTransactionDate()));
        RulesChecker.checkRule(new CheckPaymentAmountGreaterThanZeroRule(command.getPaymentAmount()));

        ManagePaymentSourceDto paymentSourceDto = this.sourceService.findById(command.getPaymentSource());
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findById(command.getPaymentStatus());
        ManageClientDto clientDto = this.clientService.findById(command.getClient());
        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
        ManageBankAccountDto bankAccountDto = this.bankAccountService.findById(command.getBankAccount());
        ManagePaymentAttachmentStatusDto attachmentStatusDto = this.attachmentStatusService.findById(command.getAttachmentStatus());

        this.paymentService.create(new PaymentDto(
                command.getId(), 
                Long.MIN_VALUE,
                command.getStatus(), 
                paymentSourceDto, 
                command.getReference(), 
                command.getTransactionDate(), 
                paymentStatusDto, 
                clientDto, 
                agencyDto, 
                hotelDto, 
                bankAccountDto, 
                attachmentStatusDto, 
                command.getPaymentAmount(), 
                command.getPaymentAmount(), 
                0.0, 
                0.0, 
                0.0, 
                0.0, 
                command.getPaymentAmount(), 
                command.getRemark()
        ));
    }
}
