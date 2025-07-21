package com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class InnsistConnectionParamsResponse implements IResponse {
    private UUID id;
    private String hostName;
    private int portNumber;
    private String dataBaseName;
    private String userName;
    private String password;
    private String description;
    private String status;
    private boolean deleted;
    private LocalDateTime updatedAt;

    public InnsistConnectionParamsResponse(InnsistConnectionParamsDto dto)
    {
        this.id = dto.getId();
        this.hostName = dto.getHostName();
        this.dataBaseName = dto.getDatabaseName();
        this.portNumber = dto.getPortNumber();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.deleted = dto.isDeleted();
        this.updatedAt = dto.getUpdatedAt();
    }
}
