package com.kynsof.identity.application.command.permission.update;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.identity.domain.rules.permission.PermissionCodeMustBeUniqueRule;
import com.kynsof.identity.infrastructure.services.kafka.producer.permission.ProducerReplicateManagePermissionService;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePermissionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.UpdateIfNotNull;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class UpdatePermissionCommandHandler implements ICommandHandler<UpdatePermissionCommand> {

    private final IPermissionService service;
    private final IModuleService serviceModule;
    private final ProducerReplicateManagePermissionService replicateManagePermissionService;

    public UpdatePermissionCommandHandler(IPermissionService service, IModuleService serviceModule, ProducerReplicateManagePermissionService replicateManagePermissionService) {
        this.service = service;
        this.serviceModule = serviceModule;
        this.replicateManagePermissionService = replicateManagePermissionService;
    }

    @Override
    public void handle(UpdatePermissionCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule(command.getId(), "id", "Permission ID cannot be null."));

        PermissionDto update = this.service.findById(command.getId());

        if (command.getCode()!= null || !command.getCode().isEmpty()) {
            RulesChecker.checkRule(new PermissionCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
            update.setCode(command.getCode());
        }

        UpdateIfNotNull.updateIfNotNull(update::setDescription, command.getDescription());

        if (command.getIdModule() != null) {
            update.setModule(this.serviceModule.findById(command.getIdModule()));
        }

        if(command.getStatus() != null){
            update.setStatus(command.getStatus());
        }

        UpdateIfNotNull.updateIfNotNull(update::setAction, command.getAction());
        UpdateIfNotNull.updateIfNotNull(update::setIsHighRisk, command.getIsHighRisk());
        UpdateIfNotNull.updateIfNotNull(update::setIsIT, command.getIsIT());
        UpdateIfNotNull.updateIfNotNull(update::setName, command.getName());

        service.update(update);

        this.replicateManagePermissionService.create(new ReplicatePermissionKafka(
                update.getId(), 
                update.getCode(), 
                update.getDescription(), 
                update.getModule().getId(), 
                update.getStatus().name(), 
                update.getAction(), 
                false, 
                LocalDateTime.now(), 
                update.getIsHighRisk(), 
                update.getIsIT(), 
                update.getName()
        ));

    }
}
