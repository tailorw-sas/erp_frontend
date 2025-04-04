package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dto.PermissionStatusEnm;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagePermissionProjection {
    private UUID id;
    private String code;
    private String description;
    private String action;
    private PermissionStatusEnm status;
    private ManageModule module;
    private LocalDateTime createdAt;
    private Boolean isHighRisk;
    private Boolean isIT;
    private String name;

    public ManagePermissionProjection(UUID id,
                                      String code,
                                      PermissionStatusEnm status,
                                      String description,
                                      String name,
                                      Boolean isHighRisk){
        this.id = id;
        this.code= code;
        this.status = status;
        this.description = description;
        this.name = name;
        this.isHighRisk = isHighRisk;
    }
}
