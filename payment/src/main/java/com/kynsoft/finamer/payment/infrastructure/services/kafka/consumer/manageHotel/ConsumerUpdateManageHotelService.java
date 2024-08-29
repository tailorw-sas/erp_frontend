package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageHotel;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageHotelKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageHotel.update.UpdateManageHotelCommand;
import com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create.CreatePaymentCloseOperationCommand;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import java.time.LocalDate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageHotelService {

    private final IMediator mediator;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public ConsumerUpdateManageHotelService(IMediator mediator,
            IPaymentCloseOperationService paymentCloseOperationService) {
        this.mediator = mediator;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @KafkaListener(topics = "finamer-update-manage-hotel", groupId = "payment-entity-replica")
    public void listen(UpdateManageHotelKafka objKafka) {
        try {
            UpdateManageHotelCommand command = new UpdateManageHotelCommand(
                    objKafka.getId(), 
                    objKafka.getName(), 
                    objKafka.getStatus(), 
                    objKafka.getApplyByTradingCompany(),
                    objKafka.getManageTradingCompany(),
                    objKafka.getAutoApplyCredit()
            );
            mediator.send(command);

            if (paymentCloseOperationService.findByHotelId(objKafka.getId()) == 0) {
                CreatePaymentCloseOperationCommand commandCloseOperationCommand = new CreatePaymentCloseOperationCommand(
                        Status.ACTIVE,
                        objKafka.getId(),
                        LocalDate.now(),
                        LocalDate.now().plusDays(1)
                );
                mediator.send(commandCloseOperationCommand);
            }

        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageHotelService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
