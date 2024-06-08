package com.kynsoft.finamer.insis.application.command.test.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.services.ITestService;
import com.kynsoft.finamer.insis.application.command.test.delete.DeleteTestCommand;
import com.kynsoft.finamer.insis.domain.dto.TestDto;
import org.springframework.stereotype.Component;

@Component
public class DeleteTestCommandHandler implements ICommandHandler<DeleteTestCommand> {

    private final ITestService service;

    public DeleteTestCommandHandler(ITestService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteTestCommand command) {
        TestDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
