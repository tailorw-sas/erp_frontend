package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.create.CreateCreditCardCloseOperationCommand;
import com.kynsoft.finamer.creditcard.application.command.manageHotel.create.CreateManageHotelCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageHotelService {

    private final IMediator mediator;

    public ConsumerReplicateManageHotelService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-hotel", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageHotelKafka entity) {
        try {
            CreateManageHotelCommand command = new CreateManageHotelCommand(entity.getId(), entity.getCode(), entity.getName(), entity.getIsApplyByVCC(), entity.getStatus());
            mediator.send(command);

            try{
                YearMonth yearMonthObject = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth());

                CreateCreditCardCloseOperationCommand closeOperationCommand= new CreateCreditCardCloseOperationCommand(
                        Status.ACTIVE,
                        entity.getId(),
                        yearMonthObject.atDay(1),
                        yearMonthObject.atEndOfMonth()
                );
                mediator.send(closeOperationCommand);
            }catch (Exception e){
                System.err.println("No se pudo crear el close operation --->>> " + e.getMessage());
            }
        } catch (Exception ex) {
            Logger.getLogger(com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageHotel.ConsumerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
