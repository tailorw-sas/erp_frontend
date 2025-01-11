package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ManageInvoiceAgencyProjection {
    // Getters y setters
    private UUID id;
    private String code;
    private String name;

    // Constructor
    public ManageInvoiceAgencyProjection(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}