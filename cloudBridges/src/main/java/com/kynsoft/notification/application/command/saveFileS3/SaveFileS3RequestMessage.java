package com.kynsoft.notification.application.command.saveFileS3;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SaveFileS3RequestMessage implements ICommandMessage {


    private final String url;
    private final UUID id;



    public SaveFileS3RequestMessage(String result, UUID id) {
        this.url = result;
        this.id = id;
    }

}
