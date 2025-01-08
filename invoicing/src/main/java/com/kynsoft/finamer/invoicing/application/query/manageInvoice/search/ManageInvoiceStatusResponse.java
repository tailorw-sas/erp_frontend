package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity.ManageInvoiceStatusProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManageInvoiceStatusResponse {

    private UUID id;
    private String code;
    private String name;
    private Boolean showClone;
    private Boolean enabledToApply;

    private boolean sentStatus;
    private boolean reconciledStatus;
    private boolean canceledStatus;
    private Boolean processStatus;

    public ManageInvoiceStatusResponse(ManageInvoiceStatusDto projection) {
        this.id = projection.getId();
        this.code = projection.getCode();
        this.name = projection.getName();
        this.showClone = projection.getShowClone();
        this.enabledToApply = projection.getEnabledToApply();
        this.sentStatus = projection.isSentStatus();
        this.reconciledStatus = projection.isReconciledStatus();
        this.canceledStatus = projection.isCanceledStatus();
        this.processStatus = projection.getProcessStatus();
    }

    public ManageInvoiceStatusResponse(ManageInvoiceStatusProjection invoiceStatus) {
        this.id = invoiceStatus.getId();
        this.code = invoiceStatus.getCode();
        this.name = invoiceStatus.getName();
        this.showClone = invoiceStatus.getShowClone();
        this.enabledToApply = invoiceStatus.getEnabledToApply();
//        this.sentStatus = invoiceStatus.getSentStatus();
//        this.reconciledStatus = invoiceStatus.getReconciledStatus();
//        this.canceledStatus = invoiceStatus.getCanceledStatus();
        this.processStatus = invoiceStatus.getProcessStatus();
    }
}
