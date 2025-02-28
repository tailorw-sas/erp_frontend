package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.ClientProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageClientSearchResponse implements IResponse{

    private UUID id;
    private String code;
    private String name;
    private String status;

    public ManageClientSearchResponse(ManageClientDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }

    public ManageClientSearchResponse(ClientProjection client) {
        this.id = client.getId();
        this.code = client.getCode();
        this.name = client.getName();
        this.status = client.getStatus();
    }
}
