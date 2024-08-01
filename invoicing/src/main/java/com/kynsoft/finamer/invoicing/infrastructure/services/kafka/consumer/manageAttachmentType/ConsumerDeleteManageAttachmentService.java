package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageAttachmentType;

import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageAttachmentService {

    private final IManageAttachmentTypeService service;

    public ConsumerDeleteManageAttachmentService(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @KafkaListener(topics = "finamer-delete-manage-attachment-type", groupId = "invoicing-entity-replica")
    public void listen(ObjectIdKafka objKafka) {
        try {
            ManageAttachmentTypeDto attachmentTypeDto = this.service.findById(objKafka.getId());
            attachmentTypeDto.setDefaults(false);
            this.service.update(attachmentTypeDto);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerDeleteManageAttachmentService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
