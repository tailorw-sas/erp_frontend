package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailMessage;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDepositToApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckGreaterThanOrEqualToTheTransactionAmountRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CreatePaymentDetailApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IManagePaymentStatusService statusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public CreatePaymentDetailApplyDepositCommandHandler(IPaymentDetailService paymentDetailService,
                                                         IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                         IPaymentService paymentService,
                                                         IManagePaymentStatusService statusService,
                                                         IPaymentCloseOperationService paymentCloseOperationService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.statusService = statusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositCommand command) {
        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(command.getAmount()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto paymentUpdate = paymentDetailDto.getPayment();

        RulesChecker.checkRule(new CheckApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));
        RulesChecker.checkRule(new CheckDepositToApplyDepositRule(paymentDetailDto.getTransactionType().getDeposit()));
        RulesChecker.checkRule(new CheckGreaterThanOrEqualToTheTransactionAmountRule(command.getAmount(), paymentDetailDto.getApplyDepositValue()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();

        UpdateIfNotNull.updateDouble(paymentUpdate::setDepositBalance, paymentUpdate.getDepositBalance() - command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setApplied, paymentUpdate.getApplied() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        PaymentDetailDto _newPaymentDetailDto = new PaymentDetailDto(
                command.getId(),
                command.getStatus(),
                paymentUpdate,
                paymentTransactionTypeDto,
                command.getAmount(),
                command.getRemark(),
                null,
                null,
                null,
                transactionDate(paymentUpdate.getHotel().getId()),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        _newPaymentDetailDto.setParentId(paymentDetailDto.getPaymentDetailId());
        this.paymentDetailService.create(_newPaymentDetailDto);

        List<PaymentDetailDto> paymentDetails = new ArrayList<>(paymentDetailDto.getPaymentDetails());
        paymentDetails.add(_newPaymentDetailDto);
        paymentDetailDto.setPaymentDetails(paymentDetails);
        paymentDetailDto.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - command.getAmount());
        paymentDetailService.update(paymentDetailDto);

        if (paymentUpdate.getPaymentBalance() == 0 && paymentUpdate.getDepositBalance() == 0) {
            paymentUpdate.setPaymentStatus(this.statusService.findByApplied());
        }
        this.paymentService.update(paymentUpdate);

        if (Objects.nonNull(command.getApplyPayment()) && command.getApplyPayment()) {
            ApplyPaymentDetailMessage message = command.getMediator().send(new ApplyPaymentDetailCommand(command.getId(),
                    command.getBooking(), command.getEmployee()));
            paymentUpdate.setApplyPayment(message.getPayment().isApplyPayment());
            paymentUpdate.setPaymentStatus(message.getPayment().getPaymentStatus());
        }

        command.setPaymentResponse(paymentUpdate);
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }
}
