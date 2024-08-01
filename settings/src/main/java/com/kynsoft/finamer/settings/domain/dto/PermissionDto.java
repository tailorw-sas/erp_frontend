package com.kynsoft.finamer.settings.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class PermissionDto {

    private UUID id;
    private String code;
    private String description;
    private ModuleDto module;
    private PermissionStatusEnm status;
    private String action;
    private LocalDateTime createdAt;
    private Boolean isHighRisk;
    private Boolean isIT;
    private String name;

    /**
     * Usar este constructor en el create.
     *
     * @param id
     * @param code
     * @param description
     * @param module
     */
    public PermissionDto(UUID id, String code, String description,
                         ModuleDto module, String action, Boolean isHighRisk,
                         Boolean isIT, String name) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.module = module;
        this.action = action;
        this.isHighRisk = isHighRisk;
        this.isIT = isIT;
        this.name = name;
    }

    public PermissionDto(UUID id, String code, String description, Boolean isHighRisk, PermissionStatusEnm status) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.isHighRisk = isHighRisk;
        this.status = status;
    }

    /**
     * Usar este constructor en el update.
     *
     * @param id
     * @param code
     * @param description
     * @param module
     * @param status
     */
    public PermissionDto(UUID id, String code, String description,
                         ModuleDto module, PermissionStatusEnm status,
                         String action, LocalDateTime createdAt, Boolean isHighRisk,
                         Boolean isIT, String name) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.module = module;
        this.status = status;
        this.action = action;
        this.createdAt = createdAt;
        this.isHighRisk = isHighRisk;
        this.isIT = isIT;
        this.name = name;
    }

}
