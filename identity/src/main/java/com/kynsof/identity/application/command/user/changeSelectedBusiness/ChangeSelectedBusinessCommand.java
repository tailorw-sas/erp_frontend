package com.kynsof.identity.application.command.user.changeSelectedBusiness;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangeSelectedBusinessCommand implements ICommand {
    private Boolean resul;
    private final  UUID businessId;
    private final  UUID userId;

    public ChangeSelectedBusinessCommand(UUID userid, UUID businessId) {
        this.businessId = businessId;
        this.userId = userid;
    }

    public static ChangeSelectedBusinessCommand fromRequest(UUID userId, ChangeSelectedBusinessRequest request) {
        return new ChangeSelectedBusinessCommand(userId, request.getBusinessId());
    }


    @Override
    public ICommandMessage getMessage() {
        return new ChangeSelectedBusinessMessage(resul);
    }
}
