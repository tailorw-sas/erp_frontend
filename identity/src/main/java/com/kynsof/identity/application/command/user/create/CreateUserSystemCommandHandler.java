package com.kynsof.identity.application.command.user.create;

import com.kynsof.identity.application.command.auth.registrySystemUser.UserSystemKycloackRequest;
import com.kynsof.identity.domain.dto.UserStatus;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.identity.domain.rules.usersystem.ModuleEmailMustBeUniqueRule;
import com.kynsof.identity.domain.rules.usersystem.ModuleUserNameMustBeUniqueRule;
import com.kynsof.identity.infrastructure.services.kafka.producer.user.ProducerRegisterUserSystemEventService;
import com.kynsof.identity.infrastructure.services.kafka.producer.user.welcom.ProducerUserWelcomEventService;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.UserWelcomKafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateUserSystemCommandHandler implements ICommandHandler<CreateUserSystemCommand> {

    private final IUserSystemService userSystemService;
    private final IAuthService authService;

    @Autowired
    private ProducerUserWelcomEventService producerUserWelcomEventService;

    @Autowired
    private ProducerRegisterUserSystemEventService registerUserSystemEventService;

    @Autowired
    public CreateUserSystemCommandHandler(IUserSystemService userSystemService, IAuthService authService) {
        this.userSystemService = userSystemService;
        this.authService = authService;
    }

    @Override
    public void handle(CreateUserSystemCommand command) {
        RulesChecker.checkRule(new ModuleEmailMustBeUniqueRule(this.userSystemService, command.getEmail(), UUID.randomUUID()));
        RulesChecker.checkRule(new ModuleUserNameMustBeUniqueRule(this.userSystemService, command.getUserName(), UUID.randomUUID()));

        UserSystemKycloackRequest userSystemRequest = new UserSystemKycloackRequest(
                command.getUserName(),
                command.getEmail(),
                command.getName(),
                command.getLastName(),
                command.getPassword(),
                command.getUserType()
        );
        String userId = authService.registerUserSystem(userSystemRequest, true);

        UserSystemDto userDto = new UserSystemDto(
                UUID.fromString(userId),
                command.getUserName(),
                command.getEmail(),
                command.getName(),
                command.getLastName(),
                UserStatus.ACTIVE,
                command.getImage()
        );
        userDto.setUserName(command.getUserName());
        userDto.setUserType(command.getUserType());

        UUID id = userSystemService.create(userDto);
        this.registerUserSystemEventService.create(userSystemRequest, id.toString(), command.getImage());
        this.producerUserWelcomEventService.create(new UserWelcomKafka(userDto.getEmail(), 
                                                                     command.getPassword(),
                                                                     command.getUserName(), 
                                                                     command.getName() + " " + command.getLastName()
                                                                    ));
        command.setId(id);

    }
}
