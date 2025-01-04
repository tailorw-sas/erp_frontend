package com.kynsoft.finamer.settings.infrastructure.services.kafka.consumer.managePermission;

import com.kynsof.share.core.domain.kafka.entity.ReplicatePermissionKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managePermission.create.CreateManagePremissionCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CReplicateManagePermissionService {

    private final IMediator mediator;

    public CReplicateManagePermissionService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-permission", groupId = "settings-entity-replica")
    public void listen(ReplicatePermissionKafka objKafka) {
        try {

            CreateManagePremissionCommand command = new CreateManagePremissionCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getDescription(),
                    objKafka.getModule(),
                    objKafka.getStatus(),
                    objKafka.getAction(),
                    false,
                    objKafka.getCreatedAt(),
                    objKafka.getIsHighRisk(),
                    objKafka.getIsIT(),
                    objKafka.getName()
            );

            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(CReplicateManagePermissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
