package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_collection_status")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_collection_status",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageCollectionStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    private Boolean enabledPayment;
    private Boolean isVisible;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_collection_status_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageCollectionStatus> navigate = new ArrayList<>();


    public ManageCollectionStatus(ManageCollectionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.enabledPayment = dto.getEnabledPayment();
        this.isVisible = dto.getIsVisible();
        if (dto.getNavigate() != null) {
            this.navigate = dto.getNavigate().stream()
                    .map(ManageCollectionStatus::new)
                    .collect(Collectors.toList());
        }
    }

    public ManageCollectionStatusDto toAggregateSimple(){
        return new ManageCollectionStatusDto(
                id, code, description, status, name, enabledPayment, isVisible, null
        );
    }

    public ManageCollectionStatusDto toAggregate(){
        return new ManageCollectionStatusDto(
                id, code, description, status, name, enabledPayment, isVisible,
                navigate != null ? navigate.stream().map(ManageCollectionStatus::toAggregateSimple).toList() : null
        );
    }
}
