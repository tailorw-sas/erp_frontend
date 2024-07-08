package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountGreaterThanZeroStrictlyRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckMinNumberOfCharacterInRemarkRule;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailCommandHandler implements ICommandHandler<CreatePaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;

    public CreatePaymentDetailCommandHandler(IPaymentDetailService paymentDetailService,
                                             IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                             IPaymentService paymentService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
    }

    @Override
    public void handle(CreatePaymentDetailCommand command) {

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());

        ConsumerUpdate updatePayment = new ConsumerUpdate();

        //identified and notIdentified
        if (paymentTransactionTypeDto.getCash()) {
            RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyRule(command.getAmount(), paymentDto.getPaymentBalance()));
            UpdateIfNotNull.updateDouble(paymentDto::setIdentified, paymentDto.getIdentified() + command.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(paymentDto::setNotIdentified, paymentDto.getNotIdentified() - command.getAmount(), updatePayment::setUpdate);
        }

        //Other Deductions
        if (!paymentTransactionTypeDto.getCash() && !paymentTransactionTypeDto.getDeposit()) {
            UpdateIfNotNull.updateDouble(paymentDto::setOtherDeductions, paymentDto.getOtherDeductions() + command.getAmount(), updatePayment::setUpdate);
        }

        //Deposit Amount and Deposit Balance
        if (paymentTransactionTypeDto.getDeposit()) {
            // Crear regla que valide que el Amount ingresado no debe de ser mayor que el valor del Payment Balance y mayor que cero.
            UpdateIfNotNull.updateDouble(paymentDto::setDepositAmount, paymentDto.getDepositAmount() + command.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(paymentDto::setDepositBalance, paymentDto.getDepositBalance() + command.getAmount(), updatePayment::setUpdate);
            command.setAmount(command.getAmount() * -1);
        }

        if (paymentTransactionTypeDto.getRemarkRequired()) {
            RulesChecker.checkRule(new CheckMinNumberOfCharacterInRemarkRule(paymentTransactionTypeDto.getMinNumberOfCharacter(), command.getRemark()));
        }

        this.paymentDetailService.create(new PaymentDetailDto(
                command.getId(),
                command.getStatus(),
                paymentDto,
                paymentTransactionTypeDto,
                command.getAmount(),
                command.getRemark(),
                null
        ));

        if (updatePayment.getUpdate() > 0) {
            this.paymentService.update(paymentDto);
        }
        command.setPaymentResponse(paymentDto);
    }
}
