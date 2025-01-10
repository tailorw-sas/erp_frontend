package com.tailorw.tcaInnsist.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Getter
@Setter
public class CreateManageHotelCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String roomType;
    private UUID tradingCompanyId;

    public CreateManageHotelCommand(UUID id, String code, String name, String roomType, UUID tradingCompanyId){
        this.id = id;
        this.code = code;
        this.name = name;
        this.roomType = roomType;
        this.tradingCompanyId = tradingCompanyId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageHotelMessage(id);
    }
}
