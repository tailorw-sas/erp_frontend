package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_attachment_status")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_payment_attachment_status",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManagePaymentAttachmentStatus {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private ManageModule module;

    @Column
    private Boolean show;

    @Column
    private String permissionCode;

    @Column
    private String description;

    @Column(nullable = true)
    private Boolean defaults;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean nonNone;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean patWithAttachment;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean pwaWithOutAttachment;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean supported;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean otherSupport;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_payment_attachment_status_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManagePaymentAttachmentStatus> relatedStatuses = new ArrayList<>();

    public ManagePaymentAttachmentStatus(ManagePaymentAttachmentStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.module = dto.getModule() != null ? new ManageModule(dto.getModule()) : null;
        this.show = dto.getShow();
        this.defaults = dto.getDefaults();
        this.permissionCode = dto.getPermissionCode();
        this.description = dto.getDescription();
        if (dto.getRelatedStatuses() != null) {
            this.relatedStatuses = dto.getRelatedStatuses().stream()
                    .map(ManagePaymentAttachmentStatus::new)
                    .collect(Collectors.toList());
        }
        this.nonNone = dto.isNonNone();
        this.patWithAttachment = dto.isPatWithAttachment();
        this.pwaWithOutAttachment = dto.isPwaWithOutAttachment();
        this.supported = dto.isSupported();
        this.otherSupport = dto.isOtherSupport();
    }

    public ManagePaymentAttachmentStatusDto toAggregateSample() {
        return new ManagePaymentAttachmentStatusDto(
                id, code, name, status, module != null ? module.toAggregate() : null, show, defaults, permissionCode, description,
                null, nonNone, patWithAttachment, pwaWithOutAttachment, supported, otherSupport
        );
    }

    public ManagePaymentAttachmentStatusDto toAggregate() {
        return new ManagePaymentAttachmentStatusDto(
                id, code, name, status, module != null ? module.toAggregate() : null, show, defaults, permissionCode, description,
                relatedStatuses != null ? relatedStatuses.stream().map(ManagePaymentAttachmentStatus::toAggregateSample).toList() : null,
                nonNone, patWithAttachment, pwaWithOutAttachment, supported, otherSupport
        );
    }
}