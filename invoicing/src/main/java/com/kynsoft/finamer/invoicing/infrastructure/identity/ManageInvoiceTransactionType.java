package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "manage_invoice_transaction_type")
public class ManageInvoiceTransactionType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Column(nullable = true)
    private Boolean deleted = false;


    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;


    public ManageInvoiceTransactionType(ManageInvoiceTransactionTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();

        this.name = dto.getName();

    }

    public ManageInvoiceTransactionTypeDto toAggregate() {
        return new ManageInvoiceTransactionTypeDto(
                id, code, name
        );
    }
}
