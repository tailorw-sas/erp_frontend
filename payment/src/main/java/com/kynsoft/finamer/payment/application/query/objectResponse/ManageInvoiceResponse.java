package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceResponse implements IResponse {

    private UUID id;
    private Long invoiceId;
    private Long invoiceNo;
    private String invoiceNumber;
    private EInvoiceType invoiceType;
    private Double invoiceAmount;
    private Boolean autoRec;
    private ManageInvoiceResponse parent;

    public ManageInvoiceResponse(ManageInvoiceDto dto) {
        this.id = dto.getId();
        this.invoiceId = dto.getInvoiceId();
        this.invoiceNo = dto.getInvoiceNo();
        this.invoiceNumber = deleteHotelInfo(dto.getInvoiceNumber());
        this.invoiceType = dto.getInvoiceType();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.autoRec = dto.getAutoRec() != null ? dto.getAutoRec() : null;
        this.parent = dto.getParent() != null ? new ManageInvoiceResponse(dto.getParent()) : null;
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
