package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailTypeDepositCommandHandler implements ICommandHandler<CreatePaymentDetailTypeDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    public CreatePaymentDetailTypeDepositCommandHandler(IPaymentDetailService paymentDetailService, IManagePaymentTransactionTypeService paymentTransactionTypeService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
    }

    @Override
    public void handle(CreatePaymentDetailTypeDepositCommand command) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                command.getId(),
                Status.ACTIVE,
                command.getPayment(),
                this.paymentTransactionTypeService.findByDeposit(),
                command.getPayment().getPaymentAmount(),
                command.getPayment().getRemark(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        this.paymentDetailService.create(newDetailDto);
        command.setNewDetailDto(newDetailDto);
    }

}
