package com.kynsoft.finamer.payment.infrastructure.excel.event.deposit;

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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DepositEventHandler implements ApplicationListener<DepositEvent> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public DepositEventHandler(IPaymentDetailService paymentDetailService,
                               IManagePaymentTransactionTypeService paymentTransactionTypeService,
                               IPaymentService paymentService, IPaymentCloseOperationService paymentCloseOperationService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void onApplicationEvent(DepositEvent event) {
        this.handle(event);
    }

    private void handle(DepositEvent command) {
        PaymentDto paymentDto = this.paymentService.findById(command.getPaymentDto().getId());
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                paymentDto,
                this.paymentTransactionTypeService.findByDeposit(),
                command.getAmount() * -1,
                command.getRemark(),
                null,
                null,
                null,
                transactionDate(paymentDto.getHotel().getId()),
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
        newDetailDto.setCreateByCredit(false);

        this.paymentDetailService.create(newDetailDto);
        this.calculate(paymentDto, command.getAmount());
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private void calculate(PaymentDto paymentDto, double amount) {
        paymentDto.setDepositAmount(paymentDto.getDepositAmount() + amount);
        paymentDto.setDepositBalance(paymentDto.getDepositBalance() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);

        this.paymentService.update(paymentDto);
    }
}
