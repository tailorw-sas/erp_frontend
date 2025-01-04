package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageHotel.create.CreateManageHotelCommand;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create.CreatePaymentCloseOperationCommand;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageHotelService {

    private final IMediator mediator;

    public ConsumerReplicateManageHotelService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-hotel", groupId = "payment-entity-replica")
    public void listen(ReplicateManageHotelKafka objKafka) {
        try {
            CreateManageHotelCommand command = new CreateManageHotelCommand(
                    objKafka.getId(), 
                    objKafka.getCode(), 
                    objKafka.getName(), 
                    objKafka.getStatus(), 
                    objKafka.getApplyByTradingCompany(),
                    objKafka.getManageTradingCompany(),
                    objKafka.getAutoApplyCredit()
            );
            mediator.send(command);

            try {
                YearMonth yearMonthObject = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth());
                CreatePaymentCloseOperationCommand commandCloseOperationCommand = new CreatePaymentCloseOperationCommand(
                        Status.ACTIVE, 
                        objKafka.getId(), 
                        yearMonthObject.atDay(1), 
                        yearMonthObject.atEndOfMonth()
                );
                mediator.send(commandCloseOperationCommand);
            } catch (Exception e) {
                System.err.println("No se pudo crear el Close Operation!!!");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
