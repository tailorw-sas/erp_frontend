package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.rate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.rate.syncRateBetweenInvoiceDate.SyncRateBetweenInvoiceDateCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.SyncRatesBetweenInvoiceDatesKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerSyncRatesBetweenInvoiceDatesService {

    private final IMediator mediator;

    public ConsumerSyncRatesBetweenInvoiceDatesService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-innsist-sync-rates-between-dates", groupId = "tcaInnsist-replicate-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            SyncRatesBetweenInvoiceDatesKafka objKafka = mapper.readValue(message, new TypeReference<SyncRatesBetweenInvoiceDatesKafka>() {});

            SyncRateBetweenInvoiceDateCommand command = new SyncRateBetweenInvoiceDateCommand(
                    objKafka.getProcessId(),
                    objKafka.getHotel(),
                    objKafka.getStartDate(),
                    objKafka.getEndDate(),
                    objKafka.isFirstGroup(),
                    objKafka.isLastGroup());
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerSyncRatesBetweenInvoiceDatesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
