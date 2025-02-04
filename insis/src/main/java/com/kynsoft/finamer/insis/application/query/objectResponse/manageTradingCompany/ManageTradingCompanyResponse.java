package com.kynsoft.finamer.insis.application.query.objectResponse.manageTradingCompany;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams.InnsistConnectionParamsResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ManageTradingCompanyResponse implements IResponse {
    private UUID id;
    private String code;
    private String company;
    private String innsistCode;
    private String status;
    private LocalDateTime updatedAt;
    private InnsistConnectionParamsResponse innsistConnectionParam;
    private boolean hasConnection;

    public ManageTradingCompanyResponse(ManageTradingCompanyDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.company = dto.getCompany();
        this.innsistCode = dto.getInnsistCode();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
        this.innsistConnectionParam = Objects.nonNull(dto.getInnsistConnectionParams()) ? new InnsistConnectionParamsResponse(dto.getInnsistConnectionParams()) : null;
        this.hasConnection = dto.isHasConnection();
    }
}
