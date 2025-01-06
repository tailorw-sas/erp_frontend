package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.rate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ManageRateKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateRateService {

    private final IMediator mediator;

    public ConsumerReplicateRateService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-tca-rate", groupId = "innsist-entity-replica")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            ManageRateKafka rateKafka = mapper.readValue(message, new TypeReference<ManageRateKafka>() {});
            CreateRoomRateCommand command = new CreateRoomRateCommand(rateKafka.getHotelCode(),
                    rateKafka.getAgencyCode(),
                    LocalDate.parse(rateKafka.getCheckInDate(), DateTimeFormatter.ofPattern("yyyMMdd")),
                    LocalDate.parse(rateKafka.getCheckOutDate(), DateTimeFormatter.ofPattern("yyyMMdd")),
                    rateKafka.getStayDays(),
                    rateKafka.getReservationCode(),
                    rateKafka.getGuestName(),
                    rateKafka.getFirstName(),
                    rateKafka.getLastName(),
                    rateKafka.getAmount(),
                    rateKafka.getRoomTypeCode(),
                    rateKafka.getCouponNumber(),
                    rateKafka.getTotalNumberOfGuest(),
                    rateKafka.getAdults(),
                    rateKafka.getChildrens(),
                    rateKafka.getRatePlanCode(),
                    LocalDate.parse(rateKafka.getInvoicingDate(), DateTimeFormatter.ofPattern("yyyMMdd")),
                    LocalDate.parse(rateKafka.getHotelCreationDate(), DateTimeFormatter.ofPattern("yyyMMdd")),
                    rateKafka.getOriginalAmount(),
                    rateKafka.getAmountPaymentApplied(),
                    rateKafka.getRateByAdult(),
                    rateKafka.getRateByChild(),
                    rateKafka.getRemarks(),
                    rateKafka.getRoomNumber(),
                    rateKafka.getHotelInvoiceAmount(),
                    rateKafka.getHotelInvoiceNumber(),
                    rateKafka.getInvoiceFolioNumber(),
                    rateKafka.getQuote(),
                    rateKafka.getRenewalNumber(),
                    rateKafka.getRoomCategory(),
                    rateKafka.getHash());

            mediator.send(command);

        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateRateService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
