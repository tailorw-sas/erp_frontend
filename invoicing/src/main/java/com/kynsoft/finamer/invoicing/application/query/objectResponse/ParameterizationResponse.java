package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ParameterizationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParameterizationResponse implements IResponse {

    private UUID id;
    private Boolean isActive;
    private String sent;
    private String reconciled;
    private String processed;
    private String canceled;
    private String pending;

    public ParameterizationResponse(ParameterizationDto dto){
        this.id = dto.getId();
        this.isActive = dto.getIsActive();
        this.sent = dto.getSent();
        this.reconciled = dto.getReconciled();
        this.processed = dto.getProcessed();
        this.canceled = dto.getCanceled();
        this.pending = dto.getPending();
    }
}
