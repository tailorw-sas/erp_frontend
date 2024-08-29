package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageHotelKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create.CreateInvoiceCloseOperationCommand;
import com.kynsoft.finamer.invoicing.application.command.manageHotel.create.CreateManageHotelCommand;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
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

    @KafkaListener(topics = "finamer-replicate-manage-hotel", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageHotelKafka objKafka) {
        try {

            CreateManageHotelCommand command = new CreateManageHotelCommand(
                    objKafka.getId(), 
                    objKafka.getCode(),
                    objKafka.getName(), 
                    objKafka.getManageTradingCompany(), 
                    objKafka.getIsVirtual(), 
                    objKafka.getStatus(),
                    objKafka.isRequiresFlatRate(),
                    objKafka.getAutoApplyCredit()
            );
            mediator.send(command);

            try{
                YearMonth yearMonthObject = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth());

                CreateInvoiceCloseOperationCommand closeOperationCommand= new CreateInvoiceCloseOperationCommand(
                        Status.ACTIVE,
                        objKafka.getId(),
                        yearMonthObject.atDay(1),
                        yearMonthObject.atEndOfMonth()
                );
                mediator.send(closeOperationCommand);
            }catch (Exception e){
                System.err.println("No se pudo crear el close operation --->>> " + e.getMessage());
            }
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
