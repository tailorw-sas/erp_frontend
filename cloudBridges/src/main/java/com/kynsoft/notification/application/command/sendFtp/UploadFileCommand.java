package com.kynsoft.notification.application.command.sendFtp;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFileCommand implements ICommand {
    private final String fileName;
    private final byte[] fileBytes;
    private final String server;
    private final String user;
    private final String password;
    private final int port;
    private final String path;

    public UploadFileCommand(String fileName, byte[] fileBytes, String server, String user, String password, int port, String path) {
        this.fileName = fileName;
        this.fileBytes = fileBytes;
        this.server = server;
        this.user = user;
        this.password = password;
        this.port = port;
        this.path = (path == null || path.trim().isEmpty()) ? "" : path;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UploadFileMessage("File upload initiated for: " + fileName);
    }
}