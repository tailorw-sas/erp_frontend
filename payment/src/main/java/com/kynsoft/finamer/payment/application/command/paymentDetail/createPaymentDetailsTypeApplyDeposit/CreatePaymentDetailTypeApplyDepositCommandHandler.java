package com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailTypeApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailTypeApplyDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;

    public CreatePaymentDetailTypeApplyDepositCommandHandler(IPaymentDetailService paymentDetailService, 
                                                             IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                             IPaymentService paymentService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
    }

    @Override
    public void handle(CreatePaymentDetailTypeApplyDepositCommand command) {
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                command.getId(),
                Status.ACTIVE,
                command.getPayment(),
                this.paymentTransactionTypeService.findByApplyDeposit(),
                command.getAmount(),
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

        List<PaymentDetailDto> updateChildrens = new ArrayList<>();
        if (command.getParentDetailDto().getChildren() != null) {
            updateChildrens.addAll(command.getParentDetailDto().getChildren());
        }
        updateChildrens.add(newDetailDto);
        command.getParentDetailDto().setChildren(updateChildrens);
        if (command.getParentDetailDto().getApplyDepositValue() < 0) {
            command.getParentDetailDto().setApplyDepositValue(command.getParentDetailDto().getApplyDepositValue() - newDetailDto.getAmount() * -1);
        } else {
            command.getParentDetailDto().setApplyDepositValue(command.getParentDetailDto().getApplyDepositValue() - newDetailDto.getAmount());
        }
        //command.getParentDetailDto().setApplyDepositValue(command.getParentDetailDto().getApplyDepositValue() - newDetailDto.getAmount());
        //command.getParentDetailDto().setApplyDepositValue(command.getParentDetailDto().getApplyDepositValue() - (newDetailDto.getAmount() * -1));//*-1
        paymentDetailService.update(command.getParentDetailDto());

        if (command.isApplyPayment()) {
            calculate(command.getParentDetailDto().getPayment(), newDetailDto);
        }

        command.setNewDetailDto(newDetailDto);
    }

    private void calculate(PaymentDto paymentDto, PaymentDetailDto newDetailDto) {
        PaymentDto save = this.paymentService.findById(paymentDto.getId());
        save.setDepositBalance(save.getDepositBalance() - newDetailDto.getAmount());
        //paymentDto.setNotApplied(paymentDto.getNotApplied() + newDetailDto.getAmount()); // TODO: al hacer un applied deposit el notApplied aumenta.
        save.setApplied(save.getApplied() + newDetailDto.getAmount()); // TODO: Suma de trx tipo check Cash + Check Apply Deposit  en el Manage Payment Transaction Type
        save.setIdentified(save.getIdentified() + newDetailDto.getAmount());
        save.setNotIdentified(save.getPaymentAmount() - paymentDto.getIdentified());

        this.paymentService.update(save);
    }

}
