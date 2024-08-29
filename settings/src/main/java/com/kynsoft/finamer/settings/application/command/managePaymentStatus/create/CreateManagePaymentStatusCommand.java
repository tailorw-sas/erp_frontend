package com.kynsoft.finamer.settings.application.command.managePaymentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentStatusCommand implements ICommand {
    private UUID id;
    private String code;
    private String name;
    private Status status;
    private Boolean collected;
    private String description;
    private Boolean defaults;
    private Boolean applied;

    public CreateManagePaymentStatusCommand(final String code, final String name, final Status status, final Boolean collected, final String description, Boolean defaults, Boolean applied) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.status = status;
        this.collected = collected;
        this.description = description;
        this.defaults = defaults;
        this.applied = applied;
    }

    public static CreateManagePaymentStatusCommand fromRequest(CreateManagePaymentStatusRequest request) {
        return new CreateManagePaymentStatusCommand(request.getCode(), request.getName(), request.getStatus(), request.getCollected(), request.getDescription(), request.getDefaults(), request.getApplied());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentStatusMessage(id);
    }
}
