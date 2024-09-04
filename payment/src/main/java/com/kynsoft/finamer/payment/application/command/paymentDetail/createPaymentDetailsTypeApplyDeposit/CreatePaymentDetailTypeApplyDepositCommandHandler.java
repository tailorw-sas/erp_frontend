package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailTypeApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailTypeApplyDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    public CreatePaymentDetailTypeApplyDepositCommandHandler(IPaymentDetailService paymentDetailService, IManagePaymentTransactionTypeService paymentTransactionTypeService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
    }

    @Override
    public void handle(CreatePaymentDetailTypeApplyDepositCommand command) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                command.getId(),
                Status.ACTIVE,
                command.getPayment(),
                this.paymentTransactionTypeService.findByApplyDeposit(),
                command.getBooking().getInvoiceAmount(),
                command.getPayment().getRemark(),
                null,
                null,
                null,
                OffsetDateTime.now(ZoneId.of("UTC")),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        newDetailDto.setParentId(command.getParentDetailDto().getPaymentDetailId());
        this.paymentDetailService.create(newDetailDto);
        command.setNewDetailDto(newDetailDto);
    }

}
