package com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.payment.credit.CreatePaymentFromCreditService;

import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreatePaymentToCreditCommandHandler implements ICommandHandler<CreatePaymentToCreditCommand> {

    private final CreatePaymentFromCreditService createPaymentFromCreditService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    public CreatePaymentToCreditCommandHandler(CreatePaymentFromCreditService createPaymentFromCreditService,
                                               ReplicateBookingBalanceService replicateBookingBalanceService) {
        this.createPaymentFromCreditService = createPaymentFromCreditService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void handle(CreatePaymentToCreditCommand command) {
        List<ManageBookingDto> bookings = new ArrayList<>();
        this.createPaymentFromCreditService.create(command.getHotel(),
                command.getClient(),
                command.getAgency(),
                command.getEmployee(),
                command.getInvoiceDto(),
                command.getAttachments(),
                bookings);

        List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = ReplicateBookingBalanceHelper.from(bookings, false);
        this.replicateBookingBalanceService.replicateBooking(replicateBookingBalanceHelpers);
    }
}
