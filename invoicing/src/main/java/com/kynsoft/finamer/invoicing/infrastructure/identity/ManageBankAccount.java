package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "manage_bank_account")
public class ManageBankAccount implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(unique = true)
    private String accountNumber;

    @Column(name = "bank_name")
    private String nameOfBank;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel manageHotel;

    private String description;

    public ManageBankAccount(ManageBankAccountDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.accountNumber = dto.getAccountNumber();
        this.manageHotel = new ManageHotel(dto.getManageHotelDto());
        this.nameOfBank = dto.getNameOfBank();
    }

    public ManageBankAccountDto toAggregate(){
        return new ManageBankAccountDto(
                id, status, accountNumber,
                this.nameOfBank,
                manageHotel != null ? manageHotel.toAggregate() : null

        );
    }
}
