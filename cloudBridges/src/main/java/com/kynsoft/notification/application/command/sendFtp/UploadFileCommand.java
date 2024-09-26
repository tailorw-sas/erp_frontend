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
    private final String server;
    private final String user;
    private final String password;
    private final int port;

    public UploadFileCommand(FilePart file, String server, String user, String password, String port) {
        this.file = file;
        this.server = server;
        this.user = user;
        this.password = password;
        this.port = Integer.parseInt(port);
    }

    @Override
    public ICommandMessage getMessage() {
        // Puedes personalizar el mensaje si es necesario
        return new UploadFileMessage("File upload initiated.");
    }
}