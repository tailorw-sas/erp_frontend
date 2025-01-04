package com.kynsoft.notification.application.command.sendFtp;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UploadFileMessage implements ICommandMessage {

    private final String response;


    public UploadFileMessage(String response) {
        this.response = response;

    }


}
