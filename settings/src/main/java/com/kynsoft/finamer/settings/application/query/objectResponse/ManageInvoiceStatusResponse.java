package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceStatusResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPrint;
    private Boolean enabledToPropagate;
    private Boolean enabledToApply;
    private Boolean enabledToPolicy;
    private Boolean processStatus;
    private List<ManageInvoiceStatusResponse> navigate;

    private Boolean showClone;

    public ManageInvoiceStatusResponse(ManageInvoiceStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.enabledToPrint = dto.getEnabledToPrint();
        this.enabledToPropagate = dto.getEnabledToPropagate();
        this.enabledToApply = dto.getEnabledToApply();
        this.enabledToPolicy = dto.getEnabledToPolicy();
        this.processStatus = dto.getProcessStatus();
        this.navigate = dto.getNavigate() != null ? dto.getNavigate().stream().map(ManageInvoiceStatusResponse::new).toList() : null;
        this.showClone = dto.getShowClone();
    }
}
