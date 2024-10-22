package com.kynsoft.notification.application.command.templateEntity.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.TemplateKafka;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.domain.dto.TemplateDto;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import com.kynsoft.notification.domain.service.ITemplateEntityService;
import com.kynsoft.notification.infrastructure.entity.TemplateEntity;
import com.kynsoft.notification.infrastructure.service.kafka.producer.ProducerReplicateTemplateEntityService;
import org.springframework.stereotype.Component;

@Component
public class UpdateTemplateEntityCommandHandler implements ICommandHandler<UpdateTemplateEntityCommand> {

    private final IMailjetConfigurationService mailjetConfigurationService;
    private final ITemplateEntityService templateEntityService;
    private final ProducerReplicateTemplateEntityService producerReplicateTemplateEntityService;

    public UpdateTemplateEntityCommandHandler(IMailjetConfigurationService allergyService, ITemplateEntityService templateEntityService, ProducerReplicateTemplateEntityService producerReplicateTemplateEntityService) {
        this.mailjetConfigurationService = allergyService;
        this.templateEntityService = templateEntityService;
        this.producerReplicateTemplateEntityService = producerReplicateTemplateEntityService;
    }

    @Override
    public void handle(UpdateTemplateEntityCommand command) {
        TemplateDto templateDto = templateEntityService.findById(command.getId());
        MailjetConfigurationDto mailjetConfigurationDto = this.mailjetConfigurationService.findById(command.getMailjetConfigId());
        if (command.getTemplateCode() != null) templateDto.setTemplateCode(command.getTemplateCode());
        if (command.getName() != null) templateDto.setName(command.getName());
        if (command.getDescription() != null) templateDto.setDescription(command.getDescription());
        if (command.getMailjetConfigId() != null) templateDto.setMailjetConfigurationDto(mailjetConfigurationDto);
        if (command.getLanguageCode() != null) templateDto.setLanguageCode(command.getLanguageCode());
        if (command.getType() != null) templateDto.setType(command.getType());
        templateEntityService.update(new TemplateEntity(templateDto));
        this.producerReplicateTemplateEntityService.replicate(new TemplateKafka(
                templateDto.getId(), templateDto.getTemplateCode(), templateDto.getName(),
                templateDto.getDescription(), templateDto.getMailjetConfigurationDto().getId(),
                templateDto.getCreatedAt(), templateDto.getLanguageCode(),templateDto.getType().name()
        ));
    }
}
