package com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions.CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule;
import com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions.CheckBookingListRule;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateApplyOtherDeductionsCommandHandler implements ICommandHandler<CreateApplyOtherDeductionsCommand> {

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IManageBookingService manageBookingService;
    private final IPaymentDetailService paymentDetailService;

    public CreateApplyOtherDeductionsCommandHandler(IManagePaymentTransactionTypeService paymentTransactionTypeService,
            IPaymentService paymentService,
            IManageBookingService manageBookingService,
            IPaymentDetailService paymentDetailService) {
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.manageBookingService = manageBookingService;
        this.paymentDetailService = paymentDetailService;
    }

    @Override
    public void handle(CreateApplyOtherDeductionsCommand command) {

        RulesChecker.checkRule(new CheckBookingListRule(command.getBooking()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());

        for (CreateApplyOtherDeductionsBookingRequest bookingRequest : command.getBooking()) {
            ManageBookingDto bookingDto = this.manageBookingService.findById(bookingRequest.getBookingId());

            RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule(bookingRequest.getBookingBalance(), bookingDto.getAmountBalance()));
            paymentDto.setOtherDeductions(paymentDto.getOtherDeductions() + bookingRequest.getBookingBalance());

            String remark = command.getRemark();
            if (command.getRemark().isBlank() || command.getRemark().isEmpty()) {
                remark = paymentTransactionTypeDto.getDefaultRemark();
            }

            //Deposit Amount and Deposit Balance
            PaymentDetailDto newDetailDto = new PaymentDetailDto(
                    UUID.randomUUID(),
                    Status.ACTIVE,
                    paymentDto,
                    paymentTransactionTypeDto,
                    bookingRequest.getBookingBalance(),
                    remark,
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
            command.getMediator().send(new ApplyPaymentDetailCommand(newDetailDto.getId(), bookingDto.getId()));
        }

        this.paymentService.update(paymentDto);

        command.setPaymentResponse(paymentDto);
    }
}
