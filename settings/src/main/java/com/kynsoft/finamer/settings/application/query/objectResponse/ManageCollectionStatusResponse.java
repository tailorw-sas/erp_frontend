package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCollectionStatusDto;
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
public class ManageCollectionStatusResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledPayment;
    private Boolean isVisible;
    private List<ManageCollectionStatusResponse> navigate;

    public ManageCollectionStatusResponse(ManageCollectionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.enabledPayment = dto.getEnabledPayment();
        this.isVisible = dto.getIsVisible();
        this.navigate = dto.getNavigate() != null ? dto.getNavigate().stream().map(ManageCollectionStatusResponse::new).toList() : null;
    }
}
