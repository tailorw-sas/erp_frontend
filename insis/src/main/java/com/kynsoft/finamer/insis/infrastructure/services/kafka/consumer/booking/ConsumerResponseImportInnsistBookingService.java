package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.booking;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.Errors;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnisistErrors;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.ErrorResponse;
import com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate.UpdateResponseImportRoomRateCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerResponseImportInnsistBookingService {

    private final IMediator mediator;

    public ConsumerResponseImportInnsistBookingService(IMediator mediator){
        this.mediator = mediator;
    }

//    @KafkaListener(topics = "finamer-import-innsist-response", groupId = "innsist-entity-replica")
//    public void listen(ImportInnisistErrors objKafka){
//        try{
//            UpdateResponseImportRoomRateCommand command = new UpdateResponseImportRoomRateCommand(
//                    objKafka.getImportInnsitProcessId(),
//                    objKafka.getErrors().stream().map(this::errorKafkaToErrorResponse).toList()
//            );
//
//            mediator.send(command);
//        }catch (Exception ex){
//            Logger.getLogger(ConsumerResponseImportInnsistBookingService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    private ErrorResponse errorKafkaToErrorResponse(Errors error){
//        return new ErrorResponse(
//                UUID.fromString(error.getBookingId()),
//                error.getMsg());
//    }
}
