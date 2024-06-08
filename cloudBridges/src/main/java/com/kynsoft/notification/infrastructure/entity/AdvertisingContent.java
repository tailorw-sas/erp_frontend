package com.kynsoft.notification.infrastructure.entity;

import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import com.kynsoft.notification.domain.dto.ContentType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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
public class AdvertisingContent {
    @Id
    private UUID id;

    @Column(nullable = true)
    private String title;

    @Column(nullable = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;

    @Column(nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private String url;

    @Column(nullable = true)
    private String link;

    @Column(nullable = true)
    private Boolean deleted = false;

    public AdvertisingContent(AdvertisingContentDto advertisingContentDto) {
        this.id = advertisingContentDto.getId();
        this.title = advertisingContentDto.getTitle();
        this.description = advertisingContentDto.getDescription();
        this.type = advertisingContentDto.getType();
        this.createdAt = advertisingContentDto.getCreatedAt() != null ? advertisingContentDto.getCreatedAt() : null;
        this.updatedAt = advertisingContentDto.getUpdatedAt() != null ? advertisingContentDto.getUpdatedAt() : null;
        this.url = advertisingContentDto.getUrl() != null ? advertisingContentDto.getUrl() : null;
        this.link = advertisingContentDto.getLink() != null ? advertisingContentDto.getLink() : null;
    }

    public AdvertisingContentDto toAggregate () {
        LocalDateTime createdDateTime = createdAt != null ? createdAt : null;
        LocalDateTime updateDateTime = createdAt != null ? createdAt : null;
        String urlS = url != null ? url : null;
        String linkS = link != null ? link : null;
        
        return new AdvertisingContentDto(id, title, description, type, createdDateTime, updateDateTime, urlS, linkS);
    }

}
