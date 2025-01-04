package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import jakarta.persistence.*;
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
@Table(name = "manage_agency")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_agency",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageAgency {

    @Id
    @Column(name = "id")
    private UUID id;

    private String code;

    private String name;

    private String status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "manage_agency_type_id")
    private ManageAgencyType agencyType;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_client_id")
    private ManageClient client;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageAgency(ManageAgencyDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.agencyType = dto.getAgencyType() != null ? new ManageAgencyType(dto.getAgencyType()) : null;
        this.client = dto.getClient() != null ? new ManageClient(dto.getClient()) : null;
    }

    public ManageAgencyDto toAggregate() {
        return new ManageAgencyDto(
                id, code, name, status,
                agencyType != null ? agencyType.toAggregate() : null,
                client != null ? client.toAggregate() : null
        );
    }

    public ManageAgencyDto toAggregateSample() {
        return new ManageAgencyDto(
                id, code, name, status,
                agencyType != null ? agencyType.toAggregate() : null,
                client != null ? client.toAggregate() : null
        );
    }
}
