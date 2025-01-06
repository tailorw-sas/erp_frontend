package com.kynsoft.finamer.insis.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class CreateHotelCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private LocalDateTime updatedAt;
    private UUID tradingCompany;
    private boolean deleted;

    public CreateHotelCommand(UUID id, String code, String name, String status, UUID tradingCompany){
        this.id = Objects.nonNull(id) ? id:UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.tradingCompany = tradingCompany;
        this.deleted = false;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateHotelMessage(id);
    }
}
