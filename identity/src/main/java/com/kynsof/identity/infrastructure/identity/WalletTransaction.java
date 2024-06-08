package com.kynsof.identity.infrastructure.identity;


import com.kynsof.identity.domain.dto.WalletTransactionDto;
import com.kynsof.identity.domain.dto.enumType.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String requestId;

    @Column(nullable = true)
    private String authorizationCode;

    public WalletTransaction(WalletTransactionDto dto) {
        this.id = dto.getId();
        this.wallet = new Wallet(dto.getWalletDto());
        this.amount = dto.getAmount();
        this.type = dto.getType();
        this.transactionDate = dto.getTransactionDate();
        this.description = dto.getDescription();
        this.requestId = dto.getRequestId();
        this.authorizationCode = dto.getAuthorizationCode();
    }

    // Method to convert the entity to its corresponding DTO
    public WalletTransactionDto toAggregate() {
        WalletTransactionDto dto = new WalletTransactionDto();
        dto.setId(this.id);
        dto.setWalletId(this.wallet != null ? this.wallet.getId() : null); // Ensure wallet is not null before trying to get its ID
        dto.setAmount(this.amount);
        dto.setType(this.type);
        dto.setTransactionDate(this.transactionDate);
        dto.setDescription(this.description);
        dto.setRequestId(this.requestId);
        dto.setAuthorizationCode(this.authorizationCode);
//        assert this.wallet != null;
//        dto.setWalletDto(this.wallet.toAggregate());
        return dto;
    }

}
