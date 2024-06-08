package com.kynsof.identity.application.query.walletTransaction.getbyid;

import com.kynsof.identity.domain.dto.WalletTransactionDto;
import com.kynsof.identity.domain.dto.enumType.TransactionType;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class WalletTransactionByIdResponse implements IResponse, Serializable {
    private UUID id;
    private UUID walletId; // Usar solo el ID para referenciar la billetera asociada
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime transactionDate;
    private String description;
    private String requestId;
    private String authorizationCode;

    public WalletTransactionByIdResponse(WalletTransactionDto dto) {
        this.id = dto.getId();
        this.walletId = dto.getWalletId();
        this.amount = dto.getAmount();
        this.type = dto.getType();
        this.transactionDate = dto.getTransactionDate();
        this.description = dto.getDescription();
        this.requestId = dto.getRequestId();
        this.authorizationCode = dto.getAuthorizationCode();
    }

}