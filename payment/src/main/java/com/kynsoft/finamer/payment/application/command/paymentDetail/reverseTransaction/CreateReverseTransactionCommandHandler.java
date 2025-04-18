package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment.UndoApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.core.undoApplyPayment.UndoApplyPayment;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.rules.undoApplication.CheckApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.services.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.undoApplicationUpdateBooking.ProducerUndoApplicationUpdateBookingService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CreateReverseTransactionCommandHandler implements ICommandHandler<CreateReverseTransactionCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;
    private final IManageEmployeeService employeeService;
    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManagePaymentStatusService paymentStatusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBookingService bookingService;
    private final ProducerUndoApplicationUpdateBookingService producerUndoApplicationUpdateBookingService;

    public CreateReverseTransactionCommandHandler(IPaymentDetailService paymentDetailService,
                                                  IPaymentService paymentService,
                                                  IManageEmployeeService employeeService,
                                                  IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
                                                  IManagePaymentStatusService paymentStatusService,
                                                  IPaymentCloseOperationService paymentCloseOperationService,
                                                  IManageBookingService bookingService,
                                                  ProducerUndoApplicationUpdateBookingService producerUndoApplicationUpdateBookingService) {

        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
        this.employeeService = employeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentStatusService = paymentStatusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.bookingService = bookingService;
        this.producerUndoApplicationUpdateBookingService = producerUndoApplicationUpdateBookingService;
    }

    @Override
    public void handle(CreateReverseTransactionCommand command) {
        long startTime = System.nanoTime();
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto payment = paymentDetailDto.getPayment();
        PaymentStatusHistoryDto paymentStatusHistoryDto = new PaymentStatusHistoryDto();
        PaymentDetailDto parent = null;

        RulesChecker.checkRule(new CheckApplyPaymentRule(paymentDetailDto.getApplyPayment()));
        //Comprobar que la fecha sea anterior al dia actual
        //Comprobar que el paymentDetails sea de tipo Apply Deposit o Cash, pero puede ser de other deductions
        //Lo que no puede suceder es que si es other deductions cambie el estado del payment.
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
            parent = this.paymentDetailService.findByPaymentDetailId(paymentDetailDto.getParentId());
            this.addPaymentDetails(reverseFrom, parent);
            this.calculateReverseApplyDeposit(payment, paymentDetailDto);
            //this.changeStatus(payment, command.getEmployee(), paymentStatusHistoryDto);
            //this.paymentDetailService.update(reverseFrom);
            this.paymentDetailService.create(reverseFrom);
            this.paymentDetailService.update(parent);
        } else if (paymentDetailDto.getTransactionType().getCash()) {
            this.calculateReverseCash(payment, reverseFrom.getAmount());
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            this.paymentDetailService.create(reverseFrom);
        } else {
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            //this.paymentDetailService.update(reverseFrom);
            this.calculateReverseOtherDeductions(payment, reverseFrom.getAmount());
            this.paymentDetailService.create(reverseFrom);
        }

        if(this.changeStatus(payment, paymentDetailDto, command.getEmployee(), paymentStatusHistoryDto)){
            paymentAttachmentStatusHistoryService.create(paymentStatusHistoryDto);
        }

        this.paymentService.update(payment);

        paymentDetailDto.setReverseTransaction(true);
        this.paymentDetailService.update(paymentDetailDto);

        ManageBookingDto bookingDto = paymentDetailDto.getManageBooking();
        this.undoApplyPayment(paymentDetailDto, bookingDto);
        this.bookingService.update(bookingDto);

        this.replicateBooking(payment, paymentDetailDto, bookingDto);

        long endTime = System.nanoTime();
        System.out.println("*****************Tiempo:" + (endTime - startTime)/1_000_000);
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

    private void replicateBooking(PaymentDto paymentDto, PaymentDetailDto paymentDetailDto, ManageBookingDto bookingDto){
        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            this.producerUndoApplicationUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingDto.getId(), paymentDetailDto.getAmount(), paymentKafka, paymentDetailDto.getTransactionType().getApplyDeposit()));
        } catch (Exception e) {
            Logger.getLogger(CreateReverseTransactionCommandHandler.class.getName()).log(Level.SEVERE, "Error trying to replicate booking", e);
        }
    }

}
