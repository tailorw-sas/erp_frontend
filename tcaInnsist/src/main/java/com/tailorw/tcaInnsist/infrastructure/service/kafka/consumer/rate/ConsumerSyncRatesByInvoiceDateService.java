package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.rate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.SyncRatesByInvoiceDateKafka;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerSyncRatesByInvoiceDateService {

    private final IMediator mediator;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public ConsumerSyncRatesByInvoiceDateService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-sync-rates-by-date", groupId = "tcaInnsist-replicate-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            SyncRatesByInvoiceDateKafka objKafka = mapper.readValue(message, new TypeReference<SyncRatesByInvoiceDateKafka>() {});

            int count = 1;
            for(String hotel : objKafka.getHotels()){
                SycnRateByInvoiceDateCommand command = new SycnRateByInvoiceDateCommand(
                        objKafka.getProcessId(),
                        hotel,
                        LocalDate.parse(objKafka.getInvoiceDate(), DATE_FORMATTER),
                        count == 1,
                        objKafka.getHotels().size() == count
                );
                mediator.send(command);
                count++;
            }

        }catch (Exception ex){
            Logger.getLogger(ConsumerSyncRatesByInvoiceDateService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
