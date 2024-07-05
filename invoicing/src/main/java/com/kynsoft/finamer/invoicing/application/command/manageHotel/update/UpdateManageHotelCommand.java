package com.kynsoft.finamer.invoicing.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageHotelCommand implements ICommand {

    private UUID id;
    private String name;
    private UUID tradingCompany;

    public UpdateManageHotelCommand(UUID id, String name, UUID tradingCompany) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageHotelMessage(id);
    }
}
