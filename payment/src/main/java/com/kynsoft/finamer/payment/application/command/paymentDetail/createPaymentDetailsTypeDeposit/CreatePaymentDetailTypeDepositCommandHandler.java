package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class CreatePaymentDetailTypeDepositCommandHandler implements ICommandHandler<CreatePaymentDetailTypeDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public CreatePaymentDetailTypeDepositCommandHandler(IPaymentDetailService paymentDetailService,
                                                        IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                        IPaymentCloseOperationService paymentCloseOperationService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void handle(CreatePaymentDetailTypeDepositCommand command) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                command.getId(),
                Status.ACTIVE,
                command.getPayment(),
                this.paymentTransactionTypeService.findByDeposit(),
                command.getPayment().getPaymentAmount() * -1,
                command.getPayment().getRemark(),
                null,
                null,
                null,
                transactionDate(command.getPayment().getHotel().getId()),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        newDetailDto.setApplyDepositValue(newDetailDto.getAmount() * -1);
        newDetailDto.setTransactionDate(OffsetDateTime.now(ZoneId.of("UTC")));
        newDetailDto.setCreateByCredit(command.isCreateByCredit());
        newDetailDto.getPayment().setHasDetailTypeDeposit(true);

        PaymentDetailDto save = this.paymentDetailService.create(newDetailDto);
        command.setNewDetailDto(save);
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }
}
