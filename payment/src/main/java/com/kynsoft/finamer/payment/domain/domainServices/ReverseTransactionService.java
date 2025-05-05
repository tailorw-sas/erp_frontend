package com.kynsoft.finamer.payment.domain.domainServices;

import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.core.undoApplyPayment.UndoApplyPayment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.services.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
    public void reverseTransaction(PaymentDetailDto paymentDetailDto,
                                    PaymentDto payment,
                                    UUID employee,
                                    ManageBookingDto booking){
        PaymentStatusHistoryDto paymentStatusHistoryDto = new PaymentStatusHistoryDto();

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
                transactionDate(paymentDetailDto.getPayment().getHotel().getId()),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        reverseFrom.setManageBooking(paymentDetailDto.getManageBooking());
        reverseFrom.setReverseTransaction(true);

        if (paymentDetailDto.getTransactionType().getApplyDeposit()) {
            reverseFrom.setReverseFrom(paymentDetailDto.getPaymentDetailId());
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            reverseFrom.setParentId(paymentDetailDto.getParentId());
            PaymentDetailDto parent = this.paymentDetailService.findByPaymentDetailId(paymentDetailDto.getParentId());

            this.addPaymentDetails(reverseFrom, parent);
            this.calculateReverseApplyDeposit(payment, paymentDetailDto);
            this.paymentDetailService.create(reverseFrom);
            this.paymentDetailService.update(parent);
        } else if (paymentDetailDto.getTransactionType().getCash()) {
            this.calculateReverseCash(payment, reverseFrom.getAmount());
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            this.paymentDetailService.create(reverseFrom);
        } else {
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            this.calculateReverseOtherDeductions(payment, reverseFrom.getAmount());
            this.paymentDetailService.create(reverseFrom);
        }

        if(this.changeStatus(payment, paymentDetailDto, employee, paymentStatusHistoryDto)){
            paymentAttachmentStatusHistoryService.create(paymentStatusHistoryDto);
        }

        this.paymentService.update(payment);

        paymentDetailDto.setReverseTransaction(true);
        this.paymentDetailService.update(paymentDetailDto);

        this.undoApplyPayment(paymentDetailDto, booking);
        this.bookingService.update(booking);
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private void addPaymentDetails(PaymentDetailDto reverseFrom, PaymentDetailDto parent) {
        List<PaymentDetailDto> _paymentDetails = new ArrayList<>(parent.getPaymentDetails());
        _paymentDetails.add(reverseFrom);
        parent.setPaymentDetails(_paymentDetails);
        parent.setApplyDepositValue(parent.getApplyDepositValue() - reverseFrom.getAmount());
    }

    private void calculateReverseApplyDeposit(PaymentDto paymentDto, PaymentDetailDto newDetailDto) {
        paymentDto.setDepositBalance(paymentDto.getDepositBalance() + newDetailDto.getAmount());
        paymentDto.setApplied(paymentDto.getApplied() - newDetailDto.getAmount());
        paymentDto.setIdentified(paymentDto.getIdentified() - newDetailDto.getAmount());
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() + newDetailDto.getAmount());
    }

    private void calculateReverseOtherDeductions(PaymentDto paymentDto, double amount) {
        paymentDto.setOtherDeductions(paymentDto.getOtherDeductions() + amount);
    }

    private void calculateReverseCash(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);
    }

    private boolean changeStatus(PaymentDto payment, PaymentDetailDto paymentDetailDto, UUID employee, PaymentStatusHistoryDto paymentStatusHistory) {

        if(paymentDetailDto.getTransactionType().getCash() || paymentDetailDto.getTransactionType().getApplyDeposit()){
            if (payment.getPaymentStatus().getApplied()) {
                payment.setPaymentStatus(this.paymentStatusService.findByConfirmed());
                this.createPaymentAttachmentStatusHistory(employee, payment, paymentStatusHistory);
                return true;
            }
        }

        return false;
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(UUID employee, PaymentDto payment, PaymentStatusHistoryDto paymentStatusHistory) {

        ManageEmployeeDto employeeDto = employee != null ? this.employeeService.findById(employee) : null;
        paymentStatusHistory.setId(UUID.randomUUID());
        paymentStatusHistory.setDescription("Update Payment.");
        paymentStatusHistory.setEmployee(employeeDto);
        paymentStatusHistory.setPayment(payment);
        paymentStatusHistory.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());
    }

    private void undoApplyPayment(PaymentDetailDto paymentDetail, ManageBookingDto booking){
        UndoApplyPayment undoApplyPayment = new UndoApplyPayment(paymentDetail, booking);
        undoApplyPayment.undoApply();
    }
}
