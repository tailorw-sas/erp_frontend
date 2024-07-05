package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import java.util.UUID;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAttachmentResponse implements IResponse {
    private UUID id;
    private Long attachment_id;
    private Long resource;
    private String filename;
    private String file;
    private String remark;
    private ManageAttachmentTypeDto type;
    private ManageInvoiceDto invoice;

    public ManageAttachmentResponse(ManageAttachmentDto dto) {
        this.id = dto.getId();
        this.remark = dto.getRemark();
        this.file = dto.getFile();
        this.filename = dto.getFilename();
        this.type = dto.getType();
        this.invoice = dto.getInvoice();
        this.attachment_id = dto.getAttachment_id();
    }
}
