package com.kynsoft.finamer.settings.application.command.manageCollectionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageCollectionStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledPayment;
    private Boolean isVisible;
    private List<UUID> navigate;

    public CreateManageCollectionStatusCommand(String code, String description, Status status,
                                               String name, Boolean enabledPayment, Boolean isVisible,
                                               List<UUID> navigate) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.enabledPayment = enabledPayment;
        this.isVisible = isVisible;
        this.navigate = navigate;
    }

    public static CreateManageCollectionStatusCommand fromRequest(CreateManageCollectionStatusRequest request){
        return new CreateManageCollectionStatusCommand(
                request.getCode(), request.getDescription(), request.getStatus(), request.getName(),
                request.getEnabledPayment(), request.getIsVisible(), request.getNavigate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageCollectionStatusMessage(id);
    }
}
