package com.kynsof.identity.application.query.module.search;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ModuleListResponse implements IResponse {
    private UUID id;
    private String name;
    private String description;
    private String image;
    private LocalDate createdAt;


    public ModuleListResponse(ModuleDto object) {
        this.id = object.getId();
        this.name = object.getName();
        this.description = object.getDescription();
        this.image = object.getImage();
        this.createdAt = object.getCreatedAt().toLocalDate();
    }

}