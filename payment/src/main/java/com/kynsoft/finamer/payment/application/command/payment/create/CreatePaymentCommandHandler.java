package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.application.command.manageEmployee.create.CreateManageEmployeeCommand;
import com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee.ManageEmployeeRequest;
import com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee.ManageEmployeeResponse;
import com.kynsoft.finamer.payment.application.services.payment.create.CreatePaymentService;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.payment.CheckIfTransactionDateIsWithInRangeCloseOperationRule;
import com.kynsoft.finamer.payment.domain.rules.payment.PaymentValidateBankAccountAndHotelRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckIfDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.ManageEmployeeHttpService;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreatePaymentCommandHandler implements ICommandHandler<CreatePaymentCommand> {

    private final CreatePaymentService createPaymentService;

    public CreatePaymentCommandHandler(CreatePaymentService createPaymentService) {
        this.createPaymentService = createPaymentService;
    }

    @Override
    public void handle(CreatePaymentCommand command){
        PaymentDto paymentDto = this.createPaymentService.create(command.getPaymentSource(),
                command.getPaymentStatus(),
                command.getHotel(),
                command.getClient(),
                command.getAgency(),
                command.isIgnoreBankAccount(),
                command.getBankAccount(),
                command.getPaymentAmount(),
                command.getRemark(),
                command.getReference(),
                command.getEmployee(),
                command.getAttachments()
                );

        command.setPayment(paymentDto);
    }
}
