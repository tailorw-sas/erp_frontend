package com.kynsoft.finamer.payment.application.command.test.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.TestDto;
import com.kynsoft.finamer.payment.domain.rules.test.TestUserNameMustBeNullRule;
import com.kynsoft.finamer.payment.domain.rules.test.TestUserNameMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.ITestService;
import org.springframework.stereotype.Component;

@Component
public class CreateTestCommandHandler implements ICommandHandler<CreateTestCommand> {

    private final ITestService service;

    public CreateTestCommandHandler(ITestService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateTestCommand command) {
        RulesChecker.checkRule(new TestUserNameMustBeNullRule(command.getUserName()));
        RulesChecker.checkRule(new TestUserNameMustBeUniqueRule(this.service, command.getUserName(), command.getId()));

        service.create(new TestDto(
                command.getId(),
                command.getUserName())
        );
    }
}
