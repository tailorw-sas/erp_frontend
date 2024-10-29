package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_invoice_type")
public class ManageInvoiceType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;

    private Boolean enabledToPolicy;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean invoice;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean credit;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean income;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public ManageInvoiceType(ManageInvoiceTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.enabledToPolicy = dto.getEnabledToPolicy();
        this.invoice = dto.isInvoice();
        this.credit = dto.isCredit();
        this.income = dto.isIncome();
    }

    public ManageInvoiceTypeDto toAggregate() {
        return new ManageInvoiceTypeDto(
                id, code, description, status, name, enabledToPolicy, income, credit, invoice
        );
    }

}
