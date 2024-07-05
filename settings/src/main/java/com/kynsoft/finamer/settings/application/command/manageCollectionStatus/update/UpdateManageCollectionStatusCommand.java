package com.kynsoft.finamer.settings.application.command.manageCollectionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageCollectionStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledPayment;
    private Boolean isVisible;
    private List<UUID> navigate;

    public static UpdateManageCollectionStatusCommand fromRequest(UpdateManageCollectionStatusRequest request, UUID id){
        return new UpdateManageCollectionStatusCommand(
                id, request.getCode(), request.getDescription(), request.getStatus(),
                request.getName(), request.getEnabledPayment(), request.getIsVisible(),
                request.getNavigate()
        );
    }
    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageCollectionStatusMessage(id);
    }
}
