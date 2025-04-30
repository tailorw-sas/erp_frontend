package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
<<<<<<< Updated upstream
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ApplyPaymentDetailService;
=======
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
>>>>>>> Stashed changes
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import org.springframework.stereotype.Component;

<<<<<<< Updated upstream
=======
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

>>>>>>> Stashed changes
@Component
public class ApplyPaymentDetailCommandHandler implements ICommandHandler<ApplyPaymentDetailCommand> {

    private final ApplyPaymentDetailService applyPaymentDetailService;

    public ApplyPaymentDetailCommandHandler(ApplyPaymentDetailService applyPaymentDetailService) {
        this.applyPaymentDetailService = applyPaymentDetailService;
    }

    @Override
    public void handle(ApplyPaymentDetailCommand command) {
<<<<<<< Updated upstream
        PaymentDto paymentDto = applyPaymentDetailService.applyDetail(command);
=======
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBooking(), "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentDetail(), "id", "Payment Detail ID cannot be null."));

        ManageBookingDto bookingDto = this.getBookingDto(command.getBooking());

        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());

        bookingDto.setAmountBalance(bookingDto.getAmountBalance() - paymentDetailDto.getAmount());
        paymentDetailDto.setManageBooking(bookingDto);
        paymentDetailDto.setApplyPayment(Boolean.TRUE);
        paymentDetailDto.setAppliedAt(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetailDto.setEffectiveDate(transactionDate(paymentDetailDto.getPayment().getHotel().getId()));

        this.manageBookingService.update(bookingDto);
        this.paymentDetailService.update(paymentDetailDto);

        PaymentDto paymentDto = this.paymentService.findById(paymentDetailDto.getPayment().getId());
        try {
            ReplicatePaymentDetailsKafka replicatePaymentDetailsKafka = new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId());
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    replicatePaymentDetailsKafka);
            if (bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT) || bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.OLD_CREDIT)) {
                ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(bookingDto.getId(), bookingDto.getAmountBalance(), paymentKafka, false, OffsetDateTime.now());
                this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(List.of(replicateBookingKafka)));
            } else {
                ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(bookingDto.getId(), bookingDto.getAmountBalance(), paymentKafka, false, OffsetDateTime.now());
                this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(List.of(replicateBookingKafka)));
            }
        } catch (Exception e) {
        }

        if (paymentDto.getNotApplied() == 0 && paymentDto.getDepositBalance() == 0 && !bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT)) {
            paymentDto.setPaymentStatus(this.statusService.findByApplied());
            ManageEmployeeDto employeeDto = command.getEmployee() != null ? this.manageEmployeeService.findById(command.getEmployee()) : null;
            this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
        }
        paymentDto.setApplyPayment(true);//TODO APF No se porque se hace esto si se esta aplicando detalles
        this.paymentService.update(paymentDto);

>>>>>>> Stashed changes
        command.setPaymentResponse(paymentDto);
    }
}
