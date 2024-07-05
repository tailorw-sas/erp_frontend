package com.kynsof.identity.application.command.user.sendPassword;


import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.identity.infrastructure.services.kafka.producer.user.welcom.ProducerUserWelcomEventService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.UserWelcomKafka;
import org.springframework.stereotype.Component;

@Component
public class SendPasswordCommandHandler implements ICommandHandler<SendPasswordCommand> {
    private final IAuthService authService;
    private final ProducerUserWelcomEventService producerUserWelcomEventService;
    private final IUserSystemService userSystemService;

    public SendPasswordCommandHandler(IAuthService authService, ProducerUserWelcomEventService producerUserWelcomEventService,
                                      IUserSystemService userSystemService) {

        this.authService = authService;
        this.producerUserWelcomEventService = producerUserWelcomEventService;
        this.userSystemService = userSystemService;
    }

    @Override
    public void handle(SendPasswordCommand command) {
        UserSystemDto userSystemDto = userSystemService.findById(command.getId());
        Boolean result = authService.changePassword(command.getId().toString(), command.getNewPassword());
        command.setResul(result);
        this.producerUserWelcomEventService.create(new UserWelcomKafka(userSystemDto.getEmail(),
                command.getNewPassword(),
                userSystemDto.getImage(),
                userSystemDto.getName() + " " + userSystemDto.getLastName()
        ));
    }
}
