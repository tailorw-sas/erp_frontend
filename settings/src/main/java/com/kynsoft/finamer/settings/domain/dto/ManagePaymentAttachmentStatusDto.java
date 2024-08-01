package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ManagePaymentAttachmentStatusDto {
    private UUID id;
    private String code;
    private String name;
    private Status status;
    private ModuleDto module;
    private Boolean show;
    private Boolean defaults;
    private String permissionCode;
    private String description;
    private List<ManagePaymentAttachmentStatusDto> relatedStatuses;

}