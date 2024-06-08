package com.kynsof.identity.application.command.auth.sendPasswordRecoveryOtp;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendPasswordRecoveryOtpCommand implements ICommand {
    private Boolean resul;
    private final String email;



    public SendPasswordRecoveryOtpCommand(String email) {
        this.email = email;
    }

//    public static AuthenticateCommand fromRequest(CreateAllergyEntityRequest request) {
//        return new AuthenticateCommand(request.getMedicalInformationId(), request.getCode(), request.getName());
//    }


    @Override
    public ICommandMessage getMessage() {
        return new SendPasswordRecoveryOtpMessage(resul);
    }
}
