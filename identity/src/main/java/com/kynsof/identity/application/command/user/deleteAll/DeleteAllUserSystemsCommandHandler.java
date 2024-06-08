package com.kynsof.identity.application.command.user.deleteAll;

import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllUserSystemsCommandHandler implements ICommandHandler<DeleteAllUserSystemsCommand> {

    private final IUserSystemService serviceImpl;

    public DeleteAllUserSystemsCommandHandler(IUserSystemService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteAllUserSystemsCommand command) {

        serviceImpl.deleteAll(command.getIds());
    }

}
