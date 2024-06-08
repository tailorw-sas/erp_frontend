package com.kynsof.identity.application.command.userPermisionBusiness.deleteRelation;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteUserBusinessRelationMessage implements ICommandMessage {

    private final String command = "DELETE_USER_BUSINESS_RELATION";

    public DeleteUserBusinessRelationMessage() {
    }

}
