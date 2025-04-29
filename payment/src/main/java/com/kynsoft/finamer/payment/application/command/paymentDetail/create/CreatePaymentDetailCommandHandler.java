package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.managePaymentTransactionType.create.CreateManagePaymentTransactionTypeCommand;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeRequest;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessPaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.*;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.PaymentTransactionTypeHttpService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CreatePaymentDetailCommandHandler implements ICommandHandler<CreatePaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final PaymentTransactionTypeHttpService paymentTransactionTypeHttpService;
    private final IManagePaymentStatusService statusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageEmployeeService employeeService;
    private final IManageBookingService bookingService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;
    private final ProducerUpdateBookingService producerUpdateBookingService;

    public CreatePaymentDetailCommandHandler(IPaymentDetailService paymentDetailService,
                                             IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                             IPaymentService paymentService,
                                             PaymentTransactionTypeHttpService paymentTransactionTypeHttpService,
                                             IManagePaymentStatusService statusService,
                                             IPaymentCloseOperationService paymentCloseOperationService,
                                             IManageEmployeeService employeeService,
                                             IManageBookingService bookingService,
                                             IPaymentStatusHistoryService paymentStatusHistoryService,
                                             ProducerUpdateBookingService producerUpdateBookingService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.paymentTransactionTypeHttpService = paymentTransactionTypeHttpService;
        this.statusService = statusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.employeeService = employeeService;
        this.bookingService = bookingService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.producerUpdateBookingService = producerUpdateBookingService;
    }

    @Override
    public void handle(CreatePaymentDetailCommand command) {

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.getPaymenTransactionType(command.getTransactionType(), command.getMediator());

        RulesChecker.checkRule(new CheckIfNewPaymentDetailIsApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));

        PaymentDto payment = this.paymentService.findByIdCustom(command.getPayment());
        ManageEmployeeDto employee = this.employeeService.findById(command.getEmployee());
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findByApplied();
        ManageBookingDto booking = this.getBookingAndValidate(command.getApplyPayment(), command.getBooking(), command.getAmount());

        OffsetDateTime transactionDate = this.getTransactionDate(payment.getHotel().getId());

        ProcessPaymentDetail createPaymentDetail = new ProcessPaymentDetail(payment,
                command.getAmount(),
                transactionDate,
                employee,
                command.getRemark(),
                paymentTransactionTypeDto,
                paymentStatusDto,
                null
        );
        createPaymentDetail.process();
        PaymentDetailDto paymentDetail = createPaymentDetail.getDetail();

        if (command.getApplyPayment()) {
            ProcessApplyPaymentDetail applyPaymentDetail = new ProcessApplyPaymentDetail(payment,
                    paymentDetail,
                    booking,
                    transactionDate,
                    command.getAmount());
            applyPaymentDetail.process();
        }

        this.saveAndReplicateBooking(payment, paymentDetail, command.getApplyPayment(), booking, createPaymentDetail);

        command.setPaymentResponse(payment);
    }

    private ManagePaymentTransactionTypeDto getPaymenTransactionType(UUID code, IMediator mediator){
        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = null;
        try {
            paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(code);
        } catch (Exception e) {
            ManagePaymentTransactionTypeResponse response = paymentTransactionTypeHttpService.sendAccountStatement(new ManagePaymentTransactionTypeRequest(code));
            mediator.send(CreateManagePaymentTransactionTypeCommand.fromRequest(response));
            paymentTransactionTypeDto = response.createObject();
        }

        return paymentTransactionTypeDto;
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

    private void saveAndReplicateBooking(PaymentDto payment, PaymentDetailDto paymentDetail, Boolean applyPayment, ManageBookingDto booking, ProcessPaymentDetail createPaymentDetail){
        this.paymentDetailService.create(paymentDetail);
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
            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(booking.getId(), booking.getAmountBalance(), paymentKafka, false, OffsetDateTime.now()));
        } catch (Exception ex) {
            Logger.getLogger(CreatePaymentDetailCommandHandler.class.getName()).log(Level.SEVERE, "Error at replicating booking", ex);
        }
    }
}
