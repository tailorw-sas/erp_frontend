package com.kynsoft.finamer.insis.application.command.test.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.rules.test.TestUserNameMustBeUniqueRule;
import com.kynsoft.finamer.insis.domain.services.ITestService;
import com.kynsoft.finamer.insis.domain.dto.TestDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateTestCommandHandler implements ICommandHandler<UpdateTestCommand> {

    private final ITestService service;

    public UpdateTestCommandHandler(ITestService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateTestCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Module ID cannot be null."));

        TestDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setUserName, command.getUserName(), test.getUserName(), update::setUpdate)) {
            RulesChecker.checkRule(new TestUserNameMustBeUniqueRule(this.service, command.getUserName(), command.getId()));
        }
        
        if (update.getUpdate() > 0) {
            this.service.update(test);
        }

    }
}
