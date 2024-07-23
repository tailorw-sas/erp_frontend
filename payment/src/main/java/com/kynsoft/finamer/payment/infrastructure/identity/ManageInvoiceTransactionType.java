package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTransactionTypeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
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
    private String name;

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
