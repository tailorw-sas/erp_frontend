package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_attachment_status")
public class ManagePaymentAttachmentStatus {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;
    @Column(unique = true)
    private String name;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column
    private String navigate;
    @Column
    private String module;
    @Column
    private Boolean show;
    @Column
    private String permissionCode;
    @Column
    private String description;

    @Column(nullable = true)
    private Boolean deleted = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public ManagePaymentAttachmentStatus(ManagePaymentAttachmentStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.navigate = dto.getNavigate();
        this.module = dto.getModule();
        this.show = dto.getShow();
        this.permissionCode = dto.getPermissionCode();
        this.description = dto.getDescription();
    }
    
    public ManagePaymentAttachmentStatusDto toAggregate(){
        return new ManagePaymentAttachmentStatusDto(id, code, name, status, navigate, module, show, permissionCode, description);
    }
}
