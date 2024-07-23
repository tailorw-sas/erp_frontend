package com.kynsoft.finamer.creditcard.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageAgencyCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String bookingCouponFormat;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageAgencyMessage(id);
    }
}
