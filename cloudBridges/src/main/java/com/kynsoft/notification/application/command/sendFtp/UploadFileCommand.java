package com.kynsoft.notification.application.command.sendFtp;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.codec.multipart.FilePart;

@Getter
@Setter
public class UploadFileCommand implements ICommand {
    private final FilePart file;

    public UploadFileCommand(FilePart file) {
        this.file = file;
    }

    @Override
    public ICommandMessage getMessage() {
        // Puedes personalizar el mensaje si es necesario
        return new UploadFileMessage("File upload initiated.");
    }
}