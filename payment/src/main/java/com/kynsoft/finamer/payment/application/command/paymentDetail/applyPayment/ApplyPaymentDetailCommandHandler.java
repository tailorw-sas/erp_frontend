package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Component;

@Component
public class ApplyPaymentDetailCommandHandler implements ICommandHandler<ApplyPaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;
    private final IPaymentService paymentService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IManagePaymentStatusService statusService;

    public ApplyPaymentDetailCommandHandler(IPaymentDetailService paymentDetailService, 
                                            IManageBookingService manageBookingService,
                                            IPaymentService paymentService,
                                            ProducerUpdateBookingService producerUpdateBookingService,
                                            IManagePaymentStatusService statusService) {
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
        this.paymentService = paymentService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.statusService = statusService;
    }

    @Override
    public void handle(ApplyPaymentDetailCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBooking(), "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentDetail(), "id", "Payment Detail ID cannot be null."));
        ManageBookingDto bookingDto = this.manageBookingService.findById(command.getBooking());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());

        bookingDto.setAmountBalance(bookingDto.getAmountBalance() - paymentDetailDto.getAmount());
        paymentDetailDto.setManageBooking(bookingDto);
        paymentDetailDto.setApplayPayment(Boolean.TRUE);
        this.manageBookingService.update(bookingDto);
        this.paymentDetailService.update(paymentDetailDto);

        PaymentDto paymentDto = this.paymentService.findById(paymentDetailDto.getPayment().getId());
        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingDto.getId(), paymentDetailDto.getAmount(), paymentKafka));
        } catch (Exception e) {
        }

        if (paymentDto.getNotApplied() == 0 && !bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT)) {
            paymentDto.setPaymentStatus(this.statusService.findByApplied());
            this.paymentService.update(paymentDto);
        }
        command.setPaymentResponse(paymentDto);        
    }

}
