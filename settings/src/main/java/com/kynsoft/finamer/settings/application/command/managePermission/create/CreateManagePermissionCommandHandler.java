package com.kynsoft.finamer.settings.application.command.managePermission.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ModuleDto;
import com.kynsoft.finamer.settings.domain.dto.PermissionDto;
import com.kynsoft.finamer.settings.domain.dto.PermissionStatusEnm;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import com.kynsoft.finamer.settings.domain.services.IManagePermissionService;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
public class CreateManagePermissionCommandHandler implements ICommandHandler<CreateManagePremissionCommand> {

    private final IManagePermissionService service;
    private final IManageModuleService moduleService;


    public CreateManagePermissionCommandHandler(IManagePermissionService service,
                                                IManageModuleService moduleService) {

        this.service = service;
        this.moduleService = moduleService;
    }

    @Override
    public void handle(CreateManagePremissionCommand command) {

        ModuleDto moduleDto = this.moduleService.findById(command.getModule());
        service.create(new PermissionDto(
                command.getId(), 
                command.getCode(), 
                command.getDescription(), 
                moduleDto, 
                PermissionStatusEnm.valueOf(command.getStatus()), 
                command.getAction(), 
                LocalDateTime.now(), 
                command.getIsHighRisk(), 
                command.getIsIT(), 
                command.getName()
        ));
    }
}
