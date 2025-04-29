package com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessPaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule;
import com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions.CheckBookingListRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CreateApplyOtherDeductionsCommandHandler implements ICommandHandler<CreateApplyOtherDeductionsCommand> {

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IManageBookingService manageBookingService;
    private final IPaymentDetailService paymentDetailService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageEmployeeService employeeService;
    private final IManagePaymentStatusService paymentStatusService;
    private final ProducerUpdateBookingService producerUpdateBookingService;

    public CreateApplyOtherDeductionsCommandHandler(IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                    IPaymentService paymentService,
                                                    IManageBookingService manageBookingService,
                                                    IPaymentDetailService paymentDetailService, IPaymentCloseOperationService paymentCloseOperationService,
                                                    IManageEmployeeService employeeService,
                                                    IManagePaymentStatusService paymentStatusService,
                                                    ProducerUpdateBookingService producerUpdateBookingService) {
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.manageBookingService = manageBookingService;
        this.paymentDetailService = paymentDetailService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.employeeService = employeeService;
        this.paymentStatusService = paymentStatusService;
        this.producerUpdateBookingService = producerUpdateBookingService;
    }

    @Override
    public void handle(CreateApplyOtherDeductionsCommand command) {

        RulesChecker.checkRule(new CheckBookingListRule(command.getBooking()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());
        Map<UUID, Double> bookingRequestMap = new HashMap<>();
        List<ManageBookingDto> bookings = this.getBookingsFromRequest(command, bookingRequestMap);
        OffsetDateTime transactionDate = this.getTransactionDate(paymentDto.getHotel().getId());
        ManageEmployeeDto employeeDto = employeeService.findById(command.getEmployee());
        ManagePaymentStatusDto paymentStatusApplied = paymentStatusService.findByApplied();
        List<PaymentDetailDto> otherDeductionPaymentDetails = new ArrayList<>();

        for (ManageBookingDto bookingRequest : bookings) {
            Double amountRequest = bookingRequestMap.get(bookingRequest.getId());
            RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyAndLessBookingBalanceRule(amountRequest, bookingRequest.getAmountBalance()));

            ProcessPaymentDetail createPaymentDetail = new ProcessPaymentDetail(
                    paymentDto,
                    amountRequest,
                    transactionDate,
                    employeeDto,
                    command.getRemark(),
                    paymentTransactionTypeDto,
                    paymentStatusApplied,
                    null
            );
            createPaymentDetail.process();
            PaymentDetailDto otherDeductionPaymentDetail = createPaymentDetail.getDetail();
            otherDeductionPaymentDetails.add(otherDeductionPaymentDetail);

            ProcessApplyPaymentDetail applyPaymentDetail = new ProcessApplyPaymentDetail(
                    paymentDto,
                    otherDeductionPaymentDetail,
                    bookingRequest,
                    transactionDate,
                    amountRequest
            );
            applyPaymentDetail.process();
        }

        this.paymentDetailService.bulk(otherDeductionPaymentDetails);
        this.paymentService.update(paymentDto);
        this.manageBookingService.updateAllBooking(bookings);

        otherDeductionPaymentDetails.forEach(detail ->
        {
            try{
                ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                        paymentDto.getId(),
                        paymentDto.getPaymentId(),
                        new ReplicatePaymentDetailsKafka(detail.getId(), detail.getPaymentDetailId()
                        ));
                this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(detail.getManageBooking().getId(), detail.getManageBooking().getAmountBalance(), paymentKafka, false, OffsetDateTime.now()));
            }catch (Exception ex){
                Logger.getLogger(CreateApplyOtherDeductionsCommandHandler.class.getName()).log(Level.SEVERE, "Error at replicating booking. Id: " + detail.getManageBooking().getId(), ex);
            }
        });

        command.setPaymentResponse(paymentDto);
    }

    private List<ManageBookingDto> getBookingsFromRequest(CreateApplyOtherDeductionsCommand command, Map<UUID, Double> bookingRequestMap) {
        List<UUID> bookingIds = new ArrayList<>();

        command.getBooking().forEach(bookingRequest -> {
            bookingIds.add(bookingRequest.getBookingId());
            bookingRequestMap.put(bookingRequest.getBookingId(), bookingRequest.getBookingBalance());
        });

        return manageBookingService.findAllById(bookingIds);
    }


    private OffsetDateTime getTransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }
}
