package com.kynsof.identity.application.command.user.delete;

import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteUserSystemsCommandHandler implements ICommandHandler<DeleteUserSystemsCommand> {

    private final IUserSystemService serviceImpl;

    public DeleteUserSystemsCommandHandler(IUserSystemService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteUserSystemsCommand command) {

        UserSystemDto delete = this.serviceImpl.findById(command.getId());
        serviceImpl.delete(delete);
    }

}
