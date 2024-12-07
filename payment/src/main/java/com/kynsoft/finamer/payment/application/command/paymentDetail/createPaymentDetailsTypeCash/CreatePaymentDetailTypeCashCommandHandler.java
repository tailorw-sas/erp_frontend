package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class CreatePaymentDetailTypeCashCommandHandler implements ICommandHandler<CreatePaymentDetailTypeCashCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public CreatePaymentDetailTypeCashCommandHandler(IPaymentDetailService paymentDetailService, 
                                                     IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                     IPaymentService paymentService,
                                                     IPaymentCloseOperationService paymentCloseOperationService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void handle(CreatePaymentDetailTypeCashCommand command) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                command.getId(),
                Status.ACTIVE,
                command.getPaymentCash(),
                this.paymentTransactionTypeService.findByPaymentInvoice(),
                command.getInvoiceAmount(),
                command.getPaymentCash().getRemark(),
                null,
                null,
                null,
                //OffsetDateTime.now(ZoneId.of("UTC")),
                transactionDate(command.getPaymentCash().getHotel().getId()),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        newDetailDto.setCreateByCredit(command.isCreateByCredit());
        this.paymentDetailService.create(newDetailDto);
        if (command.isApplyPayment()) {
            this.calculate(command.getPaymentCash(), command.getInvoiceAmount());
        }
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private void calculate(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);

        this.paymentService.update(paymentDto);
    }
}
