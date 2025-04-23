package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.undoApplicationUpdateBooking.ProducerUndoApplicationUpdateBookingService;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class UndoApplyPaymentDetailCommandHandler implements ICommandHandler<UndoApplyPaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;
    private final IPaymentService paymentService;
    private final ProducerUndoApplicationUpdateBookingService producerUndoApplicationUpdateBookingService;
    private final IManagePaymentStatusService statusService;

    public UndoApplyPaymentDetailCommandHandler(IPaymentDetailService paymentDetailService,
            IManageBookingService manageBookingService,
            IPaymentService paymentService,
            ProducerUndoApplicationUpdateBookingService producerUndoApplicationUpdateBookingService,
            IManagePaymentStatusService statusService) {
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
        this.paymentService = paymentService;
        this.producerUndoApplicationUpdateBookingService = producerUndoApplicationUpdateBookingService;
        this.statusService = statusService;
    }

    @Override
    public void handle(UndoApplyPaymentDetailCommand command) {
        ManageBookingDto bookingDto = this.manageBookingService.findById(command.getBooking());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        /*
        Esta variable es para identificar si lo que se esta reversando es un tipo deposito.
        Dado que sie es un apply deposit y pertenece a un credit, debe de reversar el valor en el padre del credit.
        */
        boolean deposit = paymentDetailDto.getTransactionType().getApplyDeposit();

        bookingDto.setAmountBalance(bookingDto.getAmountBalance() + paymentDetailDto.getAmount());

        this.manageBookingService.update(bookingDto);
        this.paymentDetailService.update(paymentDetailDto);

        PaymentDto paymentDto = this.paymentService.findById(paymentDetailDto.getPayment().getId());
        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            this.producerUndoApplicationUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingDto.getId(), paymentDetailDto.getAmount(), paymentKafka, deposit, OffsetDateTime.now()));
        } catch (Exception e) {
        }

        command.setPaymentResponse(paymentDto);
    }

}
