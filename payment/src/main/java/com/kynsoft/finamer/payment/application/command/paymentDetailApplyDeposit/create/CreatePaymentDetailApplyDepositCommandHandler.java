package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailCommandHandler;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessPaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.*;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CreatePaymentDetailApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IManagePaymentStatusService statusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageEmployeeService employeeService;
    private final IManageBookingService bookingService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;
    private final ProducerUpdateBookingService producerUpdateBookingService;

    public CreatePaymentDetailApplyDepositCommandHandler(IPaymentDetailService paymentDetailService,
                                                         IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                         IPaymentService paymentService,
                                                         IManagePaymentStatusService statusService,
                                                         IPaymentCloseOperationService paymentCloseOperationService,
                                                         IManageEmployeeService employeeService,
                                                         IManageBookingService bookingService,
                                                         IPaymentStatusHistoryService paymentStatusHistoryService,
                                                         ProducerUpdateBookingService producerUpdateBookingService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.statusService = statusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.employeeService = employeeService;
        this.bookingService = bookingService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.producerUpdateBookingService = producerUpdateBookingService;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositCommand command) {
        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(command.getAmount()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDetailDto parentPaymentDetail = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto payment = parentPaymentDetail.getPayment();
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findByApplied();
        ManageEmployeeDto employee = this.employeeService.findById(command.getEmployee());

        RulesChecker.checkRule(new CheckApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));
        RulesChecker.checkRule(new CheckDepositToApplyDepositRule(parentPaymentDetail.getTransactionType().getDeposit()));
        RulesChecker.checkRule(new CheckGreaterThanOrEqualToTheTransactionAmountRule(command.getAmount(), parentPaymentDetail.getApplyDepositValue()));

        OffsetDateTime transactionDate = this.getTransactionDate(payment.getHotel().getId());

        ProcessPaymentDetail createPaymentDetail = new ProcessPaymentDetail(payment,
                command.getAmount(),
                transactionDate,
                employee,
                command.getRemark(),
                paymentTransactionTypeDto,
                paymentStatusDto,
                parentPaymentDetail
        );
        createPaymentDetail.process();
        PaymentDetailDto newPaymentDetail = createPaymentDetail.getDetail();

        ManageBookingDto booking = this.getBookingAndValidate(command.getApplyPayment(), command.getBooking(), command.getAmount());
        if (command.getApplyPayment()) {
            ProcessApplyPaymentDetail applyPaymentDetail = new ProcessApplyPaymentDetail(payment,
                    newPaymentDetail,
                    booking,
                    transactionDate,
                    command.getAmount());
            applyPaymentDetail.process();
        }

        this.saveAndReplicateBooking(payment, newPaymentDetail, parentPaymentDetail, command.getApplyPayment(), booking, createPaymentDetail);

        command.setPaymentResponse(payment);
    }

    private OffsetDateTime getTransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private ManageBookingDto getBookingAndValidate(Boolean applyPayment, UUID bookingId, Double amount){
        if(applyPayment){
            ManageBookingDto booking = this.bookingService.findById(bookingId);
            RulesChecker.checkRule(new CheckBookingExistsApplyPayment(applyPayment, booking));
            return booking;
        }

        return null;
    }

    private void saveAndReplicateBooking(PaymentDto payment, PaymentDetailDto paymentDetail, PaymentDetailDto parentPaymentDetail, Boolean applyPayment, ManageBookingDto booking, ProcessPaymentDetail createPaymentDetail){
        this.paymentDetailService.create(paymentDetail);
        this.paymentDetailService.update(parentPaymentDetail);
        this.paymentService.update(payment);

        if(createPaymentDetail.isPaymentApplied()){
            PaymentStatusHistoryDto paymentStatusHistoryDto = createPaymentDetail.getPaymentStatusHistory();
            this.paymentStatusHistoryService.create(paymentStatusHistoryDto);
        }

        if(applyPayment){
            this.bookingService.update(booking);
            this.replicateBooking(payment, paymentDetail, booking);
        }
    }

    private void replicateBooking(PaymentDto payment, PaymentDetailDto paymentDetail, ManageBookingDto booking){
        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    payment.getId(),
                    payment.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetail.getId(), paymentDetail.getPaymentDetailId()
                    ));
            ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(booking.getId(), booking.getAmountBalance(), paymentKafka, false, OffsetDateTime.now());
            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(List.of(replicateBookingKafka)));
        } catch (Exception ex) {
            Logger.getLogger(CreatePaymentDetailCommandHandler.class.getName()).log(Level.SEVERE, "Error at replicating booking", ex);
        }
    }
}
