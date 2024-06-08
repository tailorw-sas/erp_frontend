package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Navigate;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_invoice_status")
public class ManageInvoiceStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Column(nullable = true)
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    private Boolean enabledToPrint;
    private Boolean enabledToPropagate;
    private Boolean enabledToApply;
    private Boolean enabledToPolicy;
    private Boolean processStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "navigate_entity", joinColumns = @JoinColumn(name = "entity_id"))
    private Set<Navigate> navigate;

    public ManageInvoiceStatus(ManageInvoiceStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.enabledToPrint = dto.getEnabledToPrint();
        this.enabledToPropagate = dto.getEnabledToPropagate();
        this.enabledToApply= dto.getEnabledToApply();
        this.enabledToPolicy = dto.getEnabledToPolicy();
        this.processStatus = dto.getProcessStatus();
        this.navigate = dto.getNavigate();
    }

    public ManageInvoiceStatusDto toAggregate(){
        return new ManageInvoiceStatusDto(
                id, code, description, status, name, enabledToPrint, enabledToPropagate,
                enabledToApply, enabledToPolicy, processStatus, new HashSet<>(navigate)
        );
    }
}
