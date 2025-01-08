package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ManageInvoiceTypeProjection {
    // Getters y setters
    private UUID id;
    private String name;
    private String code;

    // Constructor
    public ManageInvoiceTypeProjection(UUID id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
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
}