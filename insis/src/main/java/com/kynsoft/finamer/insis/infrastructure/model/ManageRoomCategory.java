package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_room_category")
public class ManageRoomCategory {
    @Id
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageRoomCategory(ManageRoomCategoryDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
    }

    public ManageRoomCategoryDto toAggregate(){
        return new ManageRoomCategoryDto(
                this.id,
                this.code,
                this.name,
                this.status,
                this.updatedAt
        );
    }
}
