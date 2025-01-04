package com.kynsoft.finamer.creditcard.application.command.resendPost;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResendPostCommand implements ICommand {
    private String result;
    private Long id;

    public ResendPostCommand(Long id) {
        this.id = id;
    }

    public static ResendPostCommand fromRequest(ResendPostRequest request) {
        return new ResendPostCommand(request.getId());
    }

    @Override
    public ICommandMessage getMessage() {
        return new ResendPostMessage(result);
    }

}
