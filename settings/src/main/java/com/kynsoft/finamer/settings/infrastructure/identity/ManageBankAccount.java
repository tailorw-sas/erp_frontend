package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageBankAccountDto;
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
@Table(name = "manage_bank_account")
public class ManageBankAccount implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(unique = true)
    private String accountNumber;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id")
    private ManagerBank manageBank;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel manageHotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_type_id")
    private ManagerAccountType manageAccountType;

    private String description;

    public ManageBankAccount(ManageBankAccountDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.accountNumber = dto.getAccountNumber();
        this.manageBank = new ManagerBank(dto.getManageBank());
        this.manageHotel = new ManageHotel(dto.getManageHotel());
        this.manageAccountType = new ManagerAccountType(dto.getManageAccountType());
        this.description = dto.getDescription();
    }

    public ManageBankAccountDto toAggregate(){
        return new ManageBankAccountDto(
                id, status, accountNumber,
                manageBank != null ? manageBank.toAggregate() : null,
                manageHotel != null ? manageHotel.toAggregate() : null,
                manageAccountType != null ? manageAccountType.toAggregate() : null,
                description
        );
    }
}
