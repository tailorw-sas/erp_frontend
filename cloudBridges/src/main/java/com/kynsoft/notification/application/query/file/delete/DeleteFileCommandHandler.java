package com.kynsoft.notification.application.query.file.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsoft.notification.domain.service.IAFileService;
import org.springframework.stereotype.Component;

@Component
public class DeleteFileCommandHandler implements ICommandHandler<DeleteFileCommand> {

    private final IAFileService serviceImpl;

    public DeleteFileCommandHandler(IAFileService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteFileCommand command) {
        FileDto object = this.serviceImpl.findById(command.getId());

        serviceImpl.delete(object);
    }

}
