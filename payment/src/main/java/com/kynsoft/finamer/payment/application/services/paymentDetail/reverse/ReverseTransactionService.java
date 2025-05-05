package com.kynsoft.finamer.payment.application.services.paymentDetail.reverse;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessReversePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailReversedTransactionRule;
import com.kynsoft.finamer.payment.domain.rules.undoApplication.CheckApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.services.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class ReverseTransactionService {

    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;
    private final IManageEmployeeService employeeService;
    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManagePaymentStatusService paymentStatusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBookingService bookingService;

    @Getter
    private PaymentDto payment;

    @Getter
    private ManageBookingDto booking;

    public ReverseTransactionService(IPaymentDetailService paymentDetailService,
                                     IPaymentService paymentService,
                                     IManageEmployeeService employeeService,
                                     IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
                                     IManagePaymentStatusService paymentStatusService,
                                     IPaymentCloseOperationService paymentCloseOperationService,
                                     IManageBookingService bookingService){
        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
        this.employeeService = employeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentStatusService = paymentStatusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.bookingService = bookingService;
    }

    @Transactional
    public PaymentDetailDto reverseTransaction(UUID paymentDetailId,
                                   UUID employeeId){

        PaymentDetailDto paymentDetailToReverse = this.paymentDetailService.findById(paymentDetailId);
        this.payment = paymentDetailToReverse.getPayment();
        OffsetDateTime transactionDate = this.getTtransactionDate(this.payment.getHotel().getId());
        ManageEmployeeDto employeeDto = this.employeeService.findById(employeeId);

        PaymentStatusHistoryDto paymentStatusHistoryDto = new PaymentStatusHistoryDto();

        PaymentDetailDto parent = this.getParentPaymentDetail(paymentDetailToReverse);
        this.booking = paymentDetailToReverse.getManageBooking();
        ManagePaymentStatusDto confirmedPaymentStatus = this.paymentStatusService.findByConfirmed();

        RulesChecker.checkRule(new CheckApplyPaymentRule(paymentDetailToReverse.getApplyPayment()));
        RulesChecker.checkRule(new CheckPaymentDetailReversedTransactionRule(paymentDetailToReverse));

        ProcessReversePaymentDetail processReverseDetail = new ProcessReversePaymentDetail(paymentDetailToReverse,
                parent,
                transactionDate,
                this.payment,
                confirmedPaymentStatus,
                employeeDto,
                paymentStatusHistoryDto,
                this.booking
        );
        processReverseDetail.process();

        PaymentDetailDto paymentDetailClone = processReverseDetail.getPaymentDetailClone();

        this.saveChanges(paymentDetailClone, paymentDetailToReverse, this.payment, processReverseDetail.getIsPaymentChangeStatus(), paymentStatusHistoryDto, this.booking);

        return paymentDetailToReverse;
    }

    private PaymentDetailDto getParentPaymentDetail(PaymentDetailDto paymentDetailDto){
        if (paymentDetailDto.getTransactionType().getApplyDeposit()){
            return this.paymentDetailService.findByPaymentDetailId(paymentDetailDto.getParentId());
        }

        return null;
    }

    private OffsetDateTime getTtransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private void saveChanges(PaymentDetailDto paymentDetailClone,
                      PaymentDetailDto paymentDetailParent,
                      PaymentDto payment,
                      Boolean wasPaymentChangedStatus,
                      PaymentStatusHistoryDto paymentStatusHistory,
                      ManageBookingDto booking){
        this.paymentDetailService.create(paymentDetailClone);
        this.paymentDetailService.update(paymentDetailParent);
        this.paymentService.update(payment);

        if(wasPaymentChangedStatus){
            this.paymentAttachmentStatusHistoryService.create(paymentStatusHistory);
        }

        this.bookingService.update(booking);
    }
}
