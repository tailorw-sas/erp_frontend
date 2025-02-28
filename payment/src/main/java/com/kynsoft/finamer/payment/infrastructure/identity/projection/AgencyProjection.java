package com.kynsoft.finamer.payment.infrastructure.identity.projection;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AgencyProjection {
    private UUID id;
    private String code;
    private String name;
    private String status;
    private ManageAgencyTypeProjection manageAgencyType;

    public AgencyProjection(UUID id, String code, String name, String status, ManageAgencyTypeProjection manageAgencyType) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.manageAgencyType = manageAgencyType;
    }
    // getters...
}