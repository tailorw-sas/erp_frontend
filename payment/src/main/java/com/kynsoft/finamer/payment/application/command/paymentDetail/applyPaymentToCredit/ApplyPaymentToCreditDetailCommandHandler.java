package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPaymentToCredit;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class ApplyPaymentToCreditDetailCommandHandler implements ICommandHandler<ApplyPaymentToCreditDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;
    private final IPaymentService paymentService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public ApplyPaymentToCreditDetailCommandHandler(IPaymentDetailService paymentDetailService,
                                                    IManageBookingService manageBookingService,
                                                    IPaymentService paymentService,
                                                    ProducerUpdateBookingService producerUpdateBookingService,
                                                    IPaymentCloseOperationService paymentCloseOperationService) {
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
        this.paymentService = paymentService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void handle(ApplyPaymentToCreditDetailCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBooking(), "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentDetail(), "id", "Payment Detail ID cannot be null."));
        ManageBookingDto bookingDto = this.manageBookingService.findById(command.getBooking());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());

        double amount = paymentDetailDto.getAmount();
        bookingDto.setAmountBalance(bookingDto.getAmountBalance() - amount);
        paymentDetailDto.setManageBooking(bookingDto);
        paymentDetailDto.setApplyPayment(Boolean.TRUE);
        paymentDetailDto.setAppliedAt(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetailDto.setEffectiveDate(transactionDate(paymentDetailDto.getPayment().getHotel().getId()));

        this.manageBookingService.update(bookingDto);
        this.paymentDetailService.update(paymentDetailDto);

        try {
            PaymentDto paymentDto = this.paymentService.findById(paymentDetailDto.getPayment().getId());
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingDto.getId(), bookingDto.getAmountBalance(), paymentKafka, true));
            command.setPaymentResponse(paymentDto);
        } catch (Exception e) {
        }
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

}
