package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.booking;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistResponseKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.RoomRateResponseKafka;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.RoomRateFieldError;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.RoomRateResponse;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.UpdateResponseImportBookingCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerResponseImportInnsistBookingService {

    private final IMediator mediator;

    public ConsumerResponseImportInnsistBookingService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-import-innsist-response", groupId = "innsist-entity-replica")
    public void listen(ImportInnsistResponseKafka objKafka){
        try{
            UpdateResponseImportBookingCommand command = new UpdateResponseImportBookingCommand(
                    objKafka.getImportInnsitProcessId(),
                    objKafka.getErrors().stream().map(this::roomRateResponseKafkaToroomRateResponse).toList(),
                    objKafka.getProcessed()
            );

            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerResponseImportInnsistBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private RoomRateResponse roomRateResponseKafkaToroomRateResponse(RoomRateResponseKafka roomRateReponseKafka){
        return new RoomRateResponse(
                roomRateReponseKafka.getInnsistBookingId(),
                roomRateReponseKafka.getInnsistRoomRateId(),
                roomRateReponseKafka.getInvoiceId(),
                Objects.nonNull(roomRateReponseKafka.getErrorFields()) ? roomRateReponseKafka.getErrorFields().stream().map(this::toRoomRateFieldError).toList() : null
        );
    }

    private RoomRateFieldError toRoomRateFieldError(ErrorField errorField){
        return new RoomRateFieldError(errorField.getField(), errorField.getMessage());
    }
}
