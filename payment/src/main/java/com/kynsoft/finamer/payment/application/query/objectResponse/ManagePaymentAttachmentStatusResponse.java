package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ManagePaymentAttachmentStatusResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean defaults;

    public ManagePaymentAttachmentStatusResponse(ManagePaymentAttachmentStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.defaults = dto.getDefaults();
    }

    public ManagePaymentAttachmentStatusResponse() {
        this.code="";
        this.name="";
        this.status="";
    }
}
