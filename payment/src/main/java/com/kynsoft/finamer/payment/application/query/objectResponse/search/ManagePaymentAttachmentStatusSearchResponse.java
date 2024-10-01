package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ManagePaymentAttachmentStatusSearchResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;

    public ManagePaymentAttachmentStatusSearchResponse(ManagePaymentAttachmentStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }

    public ManagePaymentAttachmentStatusSearchResponse() {
        this.code="";
        this.name="";
    }
}
