package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ManageInvoiceStatusProjection {
    // Getters y setters
    private UUID id;
    private String name;
    private String code;
    private Boolean showClone;
    private Boolean enabledToApply;
    private Boolean processStatus;
    private boolean sentStatus;
    private boolean reconciledStatus;
    private boolean canceledStatus;

    // Constructor
    public ManageInvoiceStatusProjection(UUID id, String name, String code, Boolean showClone,
                                         Boolean enabledToApply, Boolean processStatus,
                                         boolean sentStatus, boolean reconciledStatus, boolean canceledStatus) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.showClone = showClone;
        this.enabledToApply = enabledToApply;
        this.processStatus = processStatus;
        this.sentStatus = sentStatus;
        this.reconciledStatus = reconciledStatus;
        this.canceledStatus = canceledStatus;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setShowClone(Boolean showClone) {
        this.showClone = showClone;
    }

    public void setEnabledToApply(Boolean enabledToApply) {
        this.enabledToApply = enabledToApply;
    }

    public void setProcessStatus(Boolean processStatus) {
        this.processStatus = processStatus;
    }

    public void setSentStatus(boolean sentStatus) {
        this.sentStatus = sentStatus;
    }

    public void setReconciledStatus(boolean reconciledStatus) {
        this.reconciledStatus = reconciledStatus;
    }

    public void setCanceledStatus(boolean canceledStatus) {
        this.canceledStatus = canceledStatus;
    }
}