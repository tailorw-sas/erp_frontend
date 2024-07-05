package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manager_credit_card_type")
public class ManageCreditCardType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;
    private String description;
    private Integer firstDigit;

    @ManyToMany(mappedBy = "creditCardTypes", fetch = FetchType.EAGER)
    private Set<ManageMerchantBankAccount> merchantBankAccounts = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageCreditCardType(ManageCreditCardTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.firstDigit = dto.getFirstDigit();
        this.status = dto.getStatus();
    }

    public ManageCreditCardTypeDto toAggregate() {
        return new ManageCreditCardTypeDto(id, code, name, description, firstDigit, status);
    }

}