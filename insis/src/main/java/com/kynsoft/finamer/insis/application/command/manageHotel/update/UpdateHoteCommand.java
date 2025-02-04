package com.kynsoft.finamer.insis.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class UpdateHoteCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private LocalDateTime updatedAt;
    private UUID tradingCompanyId;

    public UpdateHoteCommand(UUID id, String code, String name, String status, UUID tradingCompanyId){
        this.id = Objects.nonNull(id) ? id:UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.tradingCompanyId = tradingCompanyId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateHotelMessage(id);
    }
}
