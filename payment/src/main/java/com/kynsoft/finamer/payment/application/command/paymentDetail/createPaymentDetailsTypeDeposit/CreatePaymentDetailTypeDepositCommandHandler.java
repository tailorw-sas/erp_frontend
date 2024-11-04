package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;

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
                command.getPayment().getPaymentAmount() * -1,//El Credit viene con valor negativo, el payment se crea en positivo y aqui se multiplica por -1 para crear el Deposit negativo.
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

        newDetailDto.setApplyDepositValue(newDetailDto.getAmount());
        newDetailDto.setTransactionDate(OffsetDateTime.now(ZoneId.of("UTC")));
        newDetailDto.setCreateByCredit(command.isCreateByCredit());

        this.paymentDetailService.create(newDetailDto);
        command.setNewDetailDto(newDetailDto);
    }

}
