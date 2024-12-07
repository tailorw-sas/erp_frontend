package com.kynsoft.finamer.settings.application.command.managePermission.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManagePremissionCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private UUID module;
    private String status;
    private String action;
    private boolean deleted = false;
    private LocalDateTime createdAt;
    private Boolean isHighRisk;
    private Boolean isIT;
    private String name;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePermissionMessage(id);
    }
}
