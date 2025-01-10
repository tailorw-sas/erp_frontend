package com.kynsoft.finamer.insis.application.command.manageTradingCompany.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class CreateTradingCompanyCommand implements ICommand {

    private UUID id;
    private String code;
    private String company;
    private String innsistCode;
    private String status;

    public CreateTradingCompanyCommand(UUID id, String code, String company, String innsistCode, String status){
        this.id = Objects.nonNull(id)?id:UUID.randomUUID();
        this.code = code;
        this.company = company;
        this.innsistCode = innsistCode;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateTradingCompanyMesssage(id);
    }
}
