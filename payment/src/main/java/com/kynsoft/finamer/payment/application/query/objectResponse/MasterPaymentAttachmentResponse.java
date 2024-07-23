package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
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
public class MasterPaymentAttachmentResponse implements IResponse {

    private UUID id;
    private Status status;
    private UUID resource;
    private ResourceTypeResponse resourceType;
    private AttachmentTypeResponse attachmentType;
    private String fileName;
    private String fileWeight;
    private String path;
    private String remark;

    public MasterPaymentAttachmentResponse(MasterPaymentAttachmentDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.resource = dto.getResource() != null ? dto.getResource().getId() : null;
        this.resourceType = dto.getResourceType() != null ? new ResourceTypeResponse(dto.getResourceType()) : null;
        this.attachmentType = dto.getAttachmentType() != null ? new AttachmentTypeResponse(dto.getAttachmentType()) : null;
        this.fileName = dto.getFileName();
        this.path = dto.getPath();
        this.remark = dto.getRemark();
        this.fileWeight = dto.getFileWeight() != null ? dto.getFileWeight() : null;
    }

}
