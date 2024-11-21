package com.kynsof.identity.application.command.permission.create;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.identity.domain.rules.permission.PermissionCodeMustBeNullRule;
import com.kynsof.identity.domain.rules.permission.PermissionCodeMustBeUniqueRule;
import com.kynsof.identity.infrastructure.services.kafka.producer.permission.ProducerReplicateManagePermissionService;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePermissionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class CreatePermissionCommandHandler implements ICommandHandler<CreatePermissionCommand> {

    private final IPermissionService service;
    private final IModuleService serviceModule;
    private final ProducerReplicateManagePermissionService replicateManagePermissionService;

    public CreatePermissionCommandHandler(IPermissionService service, IModuleService serviceModule,
                                          ProducerReplicateManagePermissionService replicateManagePermissionService) {
        this.service = service;
        this.serviceModule = serviceModule;
        this.replicateManagePermissionService = replicateManagePermissionService;
    }

    @Override
    public void handle(CreatePermissionCommand command) {
        RulesChecker.checkRule(new PermissionCodeMustBeNullRule(command.getCode()));
        RulesChecker.checkRule(new PermissionCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        RulesChecker.checkRule(new ValidateObjectNotNullRule(command.getIdModule(), "Permission.module.id", "Permission.Module ID cannot be null."));
        ModuleDto module = this.serviceModule.findById(command.getIdModule());

        PermissionDto permissionDto = new PermissionDto(
                command.getId(), command.getCode(), command.getDescription(),
                module, command.getAction(), command.getIsHighRisk(), command.getIsIT(),
                command.getName());
        permissionDto.setStatus(PermissionStatusEnm.ACTIVE);

        service.create(permissionDto);

        this.replicateManagePermissionService.create(new ReplicatePermissionKafka(
                permissionDto.getId(), 
                permissionDto.getCode(), 
                permissionDto.getDescription(), 
                permissionDto.getModule().getId(), 
                permissionDto.getStatus().name(), 
                permissionDto.getAction(), 
                false, 
                LocalDateTime.now(), 
                permissionDto.getIsHighRisk(), 
                permissionDto.getIsIT(), 
                permissionDto.getName()
        ));
    }
}