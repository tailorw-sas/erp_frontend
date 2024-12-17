package com.kynsoft.finamer.invoicing.application.query.resourceType.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchResourceTypeResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private Boolean defaults;
    private boolean invoice;

    public GetSearchResourceTypeResponse(ResourceTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.defaults = dto.getDefaults();
        this.invoice = dto.isInvoice();
    }

}
