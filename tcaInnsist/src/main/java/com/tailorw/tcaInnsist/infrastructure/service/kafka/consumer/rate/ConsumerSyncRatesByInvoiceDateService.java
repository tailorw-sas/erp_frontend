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
    private Logger logger = Logger.getLogger(ConsumerSyncRatesByInvoiceDateService.class.getName());

    public ConsumerSyncRatesByInvoiceDateService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-sync-rates-by-date", groupId = "tcaInnsist-replicate-entity")
    public void listen(String message){
        try{
            logInfo(String.format("**************************************************************", message));
            logInfo(String.format("Sync Rate Process start. Data: %s", message));
            ObjectMapper mapper = new ObjectMapper();
            SyncRatesByInvoiceDateKafka objKafka = mapper.readValue(message, new TypeReference<SyncRatesByInvoiceDateKafka>() {});

            SycnRateByInvoiceDateCommand command = new SycnRateByInvoiceDateCommand(
                    objKafka.getProcessId(),
                    objKafka.getHotels(),
                    LocalDate.parse(objKafka.getInvoiceDate(), DATE_FORMATTER),
                    false
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerSyncRatesByInvoiceDateService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void logInfo(String message){
        logger.log(Level.INFO, message);
    }
}
