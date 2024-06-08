package com.kynsof.identity.application.command.auth.forwardPassword;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForwardPasswordCommand implements ICommand {
    private Boolean resul;
    private final String email;
    private  final String newPassword;
    private final String otp;



    public ForwardPasswordCommand(String email, String newPassword, String otp) {

        this.email = email;
        this.newPassword = newPassword;
        this.otp = otp;
    }

//    public static AuthenticateCommand fromRequest(CreateAllergyEntityRequest request) {
//        return new AuthenticateCommand(request.getMedicalInformationId(), request.getCode(), request.getName());
//    }


    @Override
    public ICommandMessage getMessage() {
        return new ForwardPasswordMessage(resul);
    }
}
