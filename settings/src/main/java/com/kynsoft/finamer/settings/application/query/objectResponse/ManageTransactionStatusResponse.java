package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageTransactionStatusResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private List<ManageTransactionStatusNavegateResponse> navigate = new ArrayList<>();
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;

    public ManageTransactionStatusResponse(ManageTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.enablePayment = dto.getEnablePayment();
        this.visible = dto.getVisible();
        this.status = dto.getStatus();
        if (dto.getNavigate() != null) {
            for (ManageTransactionStatusDto manageTransactionStatusDto : dto.getNavigate()) {
                this.navigate.add(new ManageTransactionStatusNavegateResponse(manageTransactionStatusDto));
            }
        }
    }

}