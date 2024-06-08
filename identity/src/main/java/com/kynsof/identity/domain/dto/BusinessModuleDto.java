package com.kynsof.identity.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BusinessModuleDto {
    private UUID id;
    private BusinessDto business;
    private ModuleDto module;
    private LocalDateTime creationDate;

    public BusinessModuleDto(UUID id, BusinessDto business, ModuleDto module) {
        this.id = id;
        this.business = business;
        this.module = module;
    }
}
