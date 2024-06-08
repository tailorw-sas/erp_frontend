package com.kynsof.identity.domain.dto;

import com.kynsof.identity.domain.dto.enumType.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionDto {

    private UUID id;
    private UUID walletId; // Usar solo el ID para referenciar la billetera asociada
    private WalletDto walletDto;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime transactionDate;
    private String description;
    private String requestId;
    private String authorizationCode;
}
