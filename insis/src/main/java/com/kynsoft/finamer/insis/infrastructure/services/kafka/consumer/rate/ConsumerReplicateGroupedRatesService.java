package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.rate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.insis.application.command.roomRate.createGrouped.CreateGroupedRatesCommand;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.GroupedRatesKafka;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ManageRateKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateGroupedRatesService {

    private final IMediator mediator;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public ConsumerReplicateGroupedRatesService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-grouped-rate", groupId = "innsist-entity-replica")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            GroupedRatesKafka objKafka = mapper.readValue(message, new TypeReference<GroupedRatesKafka>() {});

            CreateGroupedRatesCommand command = new CreateGroupedRatesCommand(
                    objKafka.getProcessId(),
                    objKafka.getHotelCode(),
                    LocalDate.parse(objKafka.getInvoiceDate(), DATE_FORMATTER),
                    objKafka.getManageRateKafkaList().stream()
                            .map(this::rateKafkaToCommand)
                            .toList(),
                    BatchType.AUTOMATIC.name()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateGroupedRatesService.class.getName()).log(Level.SEVERE, "Error at processing topic: finamer-replicate-grouped-rate from kafka. ", ex);
        }
    }

    private CreateRoomRateCommand rateKafkaToCommand(ManageRateKafka objKafka){
        return new CreateRoomRateCommand(
                objKafka.getHotelCode(),
                objKafka.getAgencyCode(),
                LocalDate.parse(objKafka.getCheckInDate(), DATE_FORMATTER),
                LocalDate.parse(objKafka.getCheckOutDate(), DATE_FORMATTER),
                objKafka.getStayDays(),
                objKafka.getReservationCode(),
                objKafka.getGuestName(),
                objKafka.getFirstName(),
                objKafka.getLastName(),
                objKafka.getAmount(),
                objKafka.getRoomTypeCode(),
                objKafka.getCouponNumber(),
                objKafka.getTotalNumberOfGuest(),
                objKafka.getAdults(),
                objKafka.getChildrens(),
                objKafka.getRatePlanCode(),
                LocalDate.parse(objKafka.getInvoicingDate(), DATE_FORMATTER),
                LocalDate.parse(objKafka.getHotelCreationDate(), DATE_FORMATTER),
                objKafka.getOriginalAmount(),
                objKafka.getAmountPaymentApplied(),
                objKafka.getRateByAdult(),
                objKafka.getRateByChild(),
                objKafka.getRemarks(),
                objKafka.getRoomNumber(),
                objKafka.getHotelInvoiceAmount(),
                objKafka.getHotelInvoiceNumber(),
                objKafka.getInvoiceFolioNumber(),
                objKafka.getQuote(),
                objKafka.getRenewalNumber(),
                objKafka.getRoomCategory(),
                objKafka.getHash()
        );
    }
}
