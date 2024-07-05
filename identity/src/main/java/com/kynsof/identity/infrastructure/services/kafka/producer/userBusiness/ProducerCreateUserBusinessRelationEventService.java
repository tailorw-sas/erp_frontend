package com.kynsof.identity.infrastructure.services.kafka.producer.userBusiness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.share.core.domain.kafka.entity.UserBusinessKafka;
import com.kynsof.share.core.domain.kafka.event.CreateEvent;
import com.kynsof.share.core.domain.kafka.event.EventType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerCreateUserBusinessRelationEventService {
    private final KafkaTemplate<String, Object> producer;

    private final IUserPermissionBusinessService userPermissionBusinessService;

    public ProducerCreateUserBusinessRelationEventService(KafkaTemplate<String, Object> producer,
                                                  IUserPermissionBusinessService userPermissionBusinessService) {
        this.producer = producer;
        this.userPermissionBusinessService = userPermissionBusinessService;
    }

    public void create(UserPermissionBusinessDto entity) {

        try {
            UserBusinessKafka event = new UserBusinessKafka(
                    entity.getUser().getId(), 
                    entity.getBusiness().getId()
            );
            Long cant = userPermissionBusinessService.countByUserAndBusiness(event.getIdUser(), event.getIdBusiness());
            if (cant == 1) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(new CreateEvent<>(event, EventType.CREATED));
                this.producer.send("finamer-user-business", json);
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProducerCreateUserBusinessRelationEventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}