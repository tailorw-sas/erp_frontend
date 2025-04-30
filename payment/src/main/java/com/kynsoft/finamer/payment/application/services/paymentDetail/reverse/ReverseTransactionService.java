package com.kynsoft.finamer.payment.application.services.paymentDetail.reverse;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction.CreateReverseTransactionCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction.CreateReverseTransactionCommandHandler;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessReverseDetail;
import com.kynsoft.finamer.payment.domain.core.undoApplyPayment.UndoApplyPayment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailReversedTransactionRule;
import com.kynsoft.finamer.payment.domain.rules.undoApplication.CheckApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ReverseTransactionService {

    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;
    private final IManageEmployeeService employeeService;
    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManagePaymentStatusService paymentStatusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBookingService bookingService;
    private final ProducerUpdateBookingService producerUpdateBookingBalanceService;

    public ReverseTransactionService(IPaymentDetailService paymentDetailService,
                                     IPaymentService paymentService,
                                     IManageEmployeeService employeeService,
                                     IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
                                     IManagePaymentStatusService paymentStatusService,
                                     IPaymentCloseOperationService paymentCloseOperationService,
                                     IManageBookingService bookingService,
                                     ProducerUpdateBookingService producerUpdateBookingBalanceService){
        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
        this.employeeService = employeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentStatusService = paymentStatusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.bookingService = bookingService;
        this.producerUpdateBookingBalanceService = producerUpdateBookingBalanceService;
    }

    @Transactional
    public void reverseTransaction(CreateReverseTransactionCommand command){

        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto payment = paymentDetailDto.getPayment();
        PaymentStatusHistoryDto paymentStatusHistoryDto = new PaymentStatusHistoryDto();
        OffsetDateTime transactionDate = this.getTtransactionDate(paymentDetailDto.getPayment().getHotel().getId());
        PaymentDetailDto parent = this.getParentPaymentDetail(paymentDetailDto);
        ManageBookingDto bookingDto = paymentDetailDto.getManageBooking();
        ManagePaymentStatusDto confirmedPaymentStatus = this.paymentStatusService.findByConfirmed();
        ManageEmployeeDto employeeDto = command.getEmployee() != null ? this.employeeService.findById(command.getEmployee()) : null;

        //Comprobar que la fecha sea anterior al dia actual
        //Comprobar que el paymentDetails sea de tipo Apply Deposit o Cash, pero puede ser de other deductions
        //Lo que no puede suceder es que si es other deductions cambie el estado del payment.

        RulesChecker.checkRule(new CheckApplyPaymentRule(paymentDetailDto.getApplyPayment()));
        RulesChecker.checkRule(new CheckPaymentDetailReversedTransactionRule(paymentDetailDto));

        ProcessReverseDetail processReverseDetail = new ProcessReverseDetail(paymentDetailDto,
                parent,
                transactionDate,
                payment,
                confirmedPaymentStatus,
                employeeDto,
                paymentStatusHistoryDto,
                bookingDto
        );
        processReverseDetail.process();

        PaymentDetailDto paymentDetailClone = processReverseDetail.getPaymentDetailClone();

        this.saveChanges(paymentDetailClone, paymentDetailDto, payment, processReverseDetail.getIsPaymentChangeStatus(), paymentStatusHistoryDto, bookingDto);

        this.replicateBooking(payment, paymentDetailDto, bookingDto);
    }

    private PaymentDetailDto getParentPaymentDetail(PaymentDetailDto paymentDetailDto){
        if (paymentDetailDto.getTransactionType().getApplyDeposit()){
            return this.paymentDetailService.findByPaymentDetailId(paymentDetailDto.getParentId());
        }

        return null;
    }

    private OffsetDateTime getTtransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private void replicateBooking(PaymentDto paymentDto, PaymentDetailDto paymentDetailDto, ManageBookingDto bookingDto){
        try {
            ReplicatePaymentDetailsKafka replicatePaymentDetailsKafka = new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId());

            ReplicatePaymentKafka replicatePaymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    replicatePaymentDetailsKafka
            );

            ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(bookingDto.getId(), bookingDto.getAmountBalance(),
                    replicatePaymentKafka,
                    paymentDetailDto.getTransactionType().getApplyDeposit(),
                    OffsetDateTime.now());

            UpdateBookingBalanceKafka updateBookingBalanceKafka = new UpdateBookingBalanceKafka(List.of(replicateBookingKafka));
            this.producerUpdateBookingBalanceService.update(updateBookingBalanceKafka);
        } catch (Exception e) {
            Logger.getLogger(CreateReverseTransactionCommandHandler.class.getName()).log(Level.SEVERE, "Error trying to replicate booking", e);
        }
    }


    @Transactional
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
