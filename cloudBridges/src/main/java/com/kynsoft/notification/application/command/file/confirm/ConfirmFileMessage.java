package com.kynsoft.notification.application.command.file.confirm;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ConfirmFileMessage implements ICommandMessage {

    private final Boolean response;


    public ConfirmFileMessage(Boolean response) {
        this.response = response;

    }


}
