package com.kynsoft.finamer.payment.infrastructure.identity.projection;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ManageAgencyTypeProjection {
    private UUID id;
    private String code;
    private String name;
    private String status;

    public ManageAgencyTypeProjection(UUID id, String code, String name, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }
}