package com.kynsof.identity.application.command.userPermisionBusiness.deleteRelation;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteUserBusinessRelationCommand implements ICommand {
    private UUID userId;
    private UUID businessId;

    @Override
    public ICommandMessage getMessage() {
        return new DeleteUserBusinessRelationMessage();
    }

}
