package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dto.PermissionStatusEnm;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageModule;
import java.time.LocalDateTime;
import java.util.UUID;

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
}
