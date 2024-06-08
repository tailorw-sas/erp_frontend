package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagePaymentAttachmentStatusResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private Status status;
    private String navigate;
    private String module;
    private Boolean show;
    private String permissionCode;
    private String description;
    
    public ManagePaymentAttachmentStatusResponse(ManagePaymentAttachmentStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.navigate = dto.getNavigate();
        this.module = dto.getModule();
        this.show = dto.getShow();
        this.permissionCode = dto.getPermissionCode();
        this.description = dto.getDescription();
    }
}
