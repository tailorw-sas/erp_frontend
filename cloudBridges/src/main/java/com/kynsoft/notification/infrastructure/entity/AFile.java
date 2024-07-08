package com.kynsoft.notification.infrastructure.entity;

import com.kynsof.share.core.domain.BaseEntity;
import com.kynsoft.notification.domain.dto.AFileDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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
    private Boolean isConfirm = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AFile(AFileDto file) {
        this.id = file.getId();
        this.name = file.getName();
        this.microServiceName = file.getMicroServiceName();
        this.url = file.getUrl();
        this.isConfirm = file.isConfirm();
    }

    public AFileDto toAggregate () {
        return new AFileDto(id, name, microServiceName, url, isConfirm);
    }
}