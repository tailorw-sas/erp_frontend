package com.kynsoft.notification.application.command.file.saveFileS3;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SaveFileS3Message implements ICommandMessage {


    private final String url;
    private final UUID id;



    public SaveFileS3Message(String result, UUID id) {
        this.url = result;
        this.id = id;
    }

}
