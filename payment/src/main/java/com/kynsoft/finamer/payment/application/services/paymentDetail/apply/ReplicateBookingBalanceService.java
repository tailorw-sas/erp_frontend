package com.kynsoft.finamer.payment.application.services.paymentDetail.apply;

import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailCommandHandler;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ReplicateBookingBalanceService {

    private final ProducerUpdateBookingService producerUpdateBookingService;

    public ReplicateBookingBalanceService(ProducerUpdateBookingService producerUpdateBookingService){
        this.producerUpdateBookingService = producerUpdateBookingService;
    }

    public void replicateBooking(List<ReplicateBookingBalanceHelper> replicateBookingBalanceList){
        try {
            List<ReplicateBookingKafka> bookingsToReplicate = new ArrayList<>();
            for(ReplicateBookingBalanceHelper replicateBooking : replicateBookingBalanceList){
                ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(
                        replicateBooking.getBoooking().getId(),
                        replicateBooking.getBoooking().getAmountBalance(),
                        replicateBooking.getApplyDeposit(),
                        OffsetDateTime.now(ZoneOffset.UTC));
                bookingsToReplicate.add(replicateBookingKafka);
            }

            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingsToReplicate));
        } catch (Exception ex) {
            Logger.getLogger(CreatePaymentDetailCommandHandler.class.getName()).log(Level.SEVERE, "Error at replicating booking", ex);
        }
    }
}
