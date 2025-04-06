package com.kynsoft.notification.application.command.file.saveFileS3;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.codec.multipart.FilePart;

import java.util.UUID;

@Getter
@Setter
public class SaveFileS3Command implements ICommand {
    private final FilePart FilePart;
    private UUID fileId;
    private String url;

    public SaveFileS3Command(FilePart multipartFile) {
        this.FilePart = multipartFile;
    }

    @Override
    public ICommandMessage getMessage() {
        return new SaveFileS3Message(url, fileId);
    }
}
