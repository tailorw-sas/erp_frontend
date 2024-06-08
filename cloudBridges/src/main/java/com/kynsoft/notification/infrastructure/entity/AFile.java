package com.kynsoft.notification.infrastructure.entity;

import com.kynsof.share.core.domain.BaseEntity;
import com.kynsoft.notification.domain.dto.AFileDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AFile extends BaseEntity {

    private String name;
    private String microServiceName;
    private String url;

    @Column(nullable = true)
    private Boolean deleted = false;

    public AFile(UUID id, String name, String microServiceName, String url) {
        this.id = id;
        this.name = name;
        this.microServiceName = microServiceName;
        this.url = url;
    }

    public AFile(AFileDto file) {
        this.id = file.getId();
        this.name = file.getName();
        this.microServiceName = file.getMicroServiceName();
        this.url = file.getUrl();
    }

    public AFileDto toAggregate () {
        return new AFileDto(id, name, microServiceName, url);
    }
}