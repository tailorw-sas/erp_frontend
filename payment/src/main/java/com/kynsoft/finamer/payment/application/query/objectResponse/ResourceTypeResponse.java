package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResourceTypeResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private Boolean defaults;
    private boolean invoice;

    public ResourceTypeResponse(ResourceTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.defaults = dto.getDefaults();
        this.invoice = dto.isInvoice();
    }

}
