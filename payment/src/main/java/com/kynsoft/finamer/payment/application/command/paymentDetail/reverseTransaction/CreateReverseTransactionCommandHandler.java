package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment.UndoApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.rules.undoApplication.CheckApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateReverseTransactionCommandHandler implements ICommandHandler<CreateReverseTransactionCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;

    public CreateReverseTransactionCommandHandler(IPaymentDetailService paymentDetailService, IPaymentService paymentService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
    }

    @Override
    public void handle(CreateReverseTransactionCommand command) {
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        RulesChecker.checkRule(new CheckApplyPaymentRule(paymentDetailDto.getApplayPayment()));
        //Comprobar que la fecha sea anterior al dia actual
        //Comprobar que el paymentDetails sea de tipo Apply Deposit o Cash
        PaymentDetailDto reverseFrom = new PaymentDetailDto(
                UUID.randomUUID(),
                paymentDetailDto.getStatus(),
                paymentDetailDto.getPayment(),
                paymentDetailDto.getTransactionType(),
                paymentDetailDto.getAmount() * -1,
                paymentDetailDto.getRemark(),
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

        reverseFrom.setManageBooking(paymentDetailDto.getManageBooking());
        reverseFrom.setApplayPayment(Boolean.TRUE);
        this.paymentDetailService.create(reverseFrom);

        if (paymentDetailDto.getTransactionType().getApplyDeposit()) {
            reverseFrom.setReverseFrom(paymentDetailDto.getPaymentDetailId());
            reverseFrom.setParentId(paymentDetailDto.getParentId());
            this.addChildren(reverseFrom, paymentDetailDto.getParentId());
            this.calculateReverseApplyDeposit(reverseFrom.getPayment(), reverseFrom);
            this.paymentDetailService.update(reverseFrom);
        } else if (paymentDetailDto.getTransactionType().getCash()) {
            this.calculateReverseCash(reverseFrom.getPayment(), reverseFrom.getAmount());
            paymentDetailDto.setTransactionDate(null);
        }

        ManageBookingDto bookingDto = paymentDetailDto.getManageBooking();
        paymentDetailDto.setManageBooking(null);
        this.paymentDetailService.update(paymentDetailDto);

        command.getMediator().send(new UndoApplyPaymentDetailCommand(command.getPaymentDetail(), bookingDto.getId()));
    }

    private void addChildren(PaymentDetailDto reverseFrom, Long parentId) {
        PaymentDetailDto parent = this.paymentDetailService.findByPaymentDetailId(parentId);
        List<PaymentDetailDto> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(parent.getChildren());
        updateChildrens.add(reverseFrom);
        parent.setChildren(updateChildrens);
        parent.setApplyDepositValue(parent.getApplyDepositValue() + reverseFrom.getAmount());

        this.paymentDetailService.update(parent);
    }

    private void calculateReverseApplyDeposit(PaymentDto paymentDto, PaymentDetailDto newDetailDto) {
        paymentDto.setDepositBalance(paymentDto.getDepositBalance() - newDetailDto.getAmount());
        paymentDto.setNotApplied(paymentDto.getNotApplied() + newDetailDto.getAmount()); // TODO: al hacer un applied deposit el notApplied aumenta.
        paymentDto.setIdentified(paymentDto.getIdentified() + newDetailDto.getAmount());
        paymentDto.setNotIdentified(paymentDto.getPaymentAmount() - paymentDto.getIdentified());

        this.paymentService.update(paymentDto);
    }

    private void calculateReverseCash(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);

        this.paymentService.update(paymentDto);
    }

}
