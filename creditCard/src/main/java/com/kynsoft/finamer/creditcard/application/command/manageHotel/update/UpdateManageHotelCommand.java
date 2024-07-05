package com.kynsoft.finamer.creditcard.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageHotelCommand implements ICommand {

    private UUID id;
    private String name;
    private Boolean isApplyByVCC;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageHotelMessage(id);
    }
}
