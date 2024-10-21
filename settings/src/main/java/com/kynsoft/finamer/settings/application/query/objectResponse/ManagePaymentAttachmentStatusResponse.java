package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
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
public class ManagePaymentAttachmentStatusResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private Status status;
    private List<ManagePaymentAttachmentStatusResponse> navigate;
    private ManageModuleResponse module;
    private Boolean show;
    private Boolean defaults;
    private String permissionCode;
    private String description;
    private boolean nonNone;
    private boolean patWithAttachment;
    private boolean pwaWithOutAttachment;
    private boolean supported;
    private boolean otherSupport;

    public ManagePaymentAttachmentStatusResponse(ManagePaymentAttachmentStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.navigate = dto.getRelatedStatuses() != null ? dto.getRelatedStatuses().stream().map(ManagePaymentAttachmentStatusResponse::new).toList() : null;
        this.module = dto.getModule() != null ? new ManageModuleResponse(dto.getModule()) : null;
        this.show = dto.getShow();
        this.defaults = dto.getDefaults();
        this.permissionCode = dto.getPermissionCode();
        this.description = dto.getDescription();
        this.nonNone = dto.isNonNone();
        this.patWithAttachment = dto.isPatWithAttachment();
        this.pwaWithOutAttachment = dto.isPwaWithOutAttachment();
        this.supported = dto.isSupported();
        this.otherSupport = dto.isOtherSupport();
    }
}
