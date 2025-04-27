package com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule;
import com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions.CheckBookingListRule;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class CreateApplyOtherDeductionsCommandHandler implements ICommandHandler<CreateApplyOtherDeductionsCommand> {

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IManageBookingService manageBookingService;
    private final IPaymentDetailService paymentDetailService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public CreateApplyOtherDeductionsCommandHandler(IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                    IPaymentService paymentService,
                                                    IManageBookingService manageBookingService,
                                                    IPaymentDetailService paymentDetailService, IPaymentCloseOperationService paymentCloseOperationService) {
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.manageBookingService = manageBookingService;
        this.paymentDetailService = paymentDetailService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void handle(CreateApplyOtherDeductionsCommand command) {

        RulesChecker.checkRule(new CheckBookingListRule(command.getBooking()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());

        for (CreateApplyOtherDeductionsBookingRequest bookingRequest : command.getBooking()) {
            ManageBookingDto bookingDto = this.manageBookingService.findById(bookingRequest.getBookingId());

            RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule(bookingRequest.getBookingBalance(), bookingDto.getAmountBalance()));
            paymentDto.setOtherDeductions(paymentDto.getOtherDeductions() + bookingRequest.getBookingBalance());

            String remark = command.getRemark();
            if (command.getRemark().isBlank()) {
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
                    transactionDate(paymentDto.getHotel().getId()),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false
            );

            this.paymentDetailService.create(newDetailDto);
            command.getMediator().send(new ApplyPaymentDetailCommand(newDetailDto.getId(), bookingDto.getId(), command.getEmployee()));
        }

        paymentDto.setApplyPayment(true);
        this.paymentService.update(paymentDto);

        command.setPaymentResponse(paymentDto);
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }
}
