package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ManageInvoiceHotelProjection {
    // Getters y setters
    private UUID id;
    private String code;
    private String name;
    private Boolean isVirtual;

    // Constructor
    public ManageInvoiceHotelProjection(UUID id, String code, String name, Boolean isVirtual) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.isVirtual = isVirtual;
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

    public void setIsVirtual(Boolean isVirtual) {
        this.isVirtual = isVirtual;
    }
}