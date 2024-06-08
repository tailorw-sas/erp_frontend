package com.kynsoft.notification.application.command.saveFileS3;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
public class SaveFileS3RequestCommand implements ICommand {
    private final MultipartFile multipartFile;
    private final String fonder;
    private UUID fileId;
    private String url;

    public SaveFileS3RequestCommand(MultipartFile multipartFile, String fonder) {


        this.multipartFile = multipartFile;
        this.fonder = fonder;
    }



    @Override
    public ICommandMessage getMessage() {
        return new SaveFileS3RequestMessage(url,fileId);
    }
}
