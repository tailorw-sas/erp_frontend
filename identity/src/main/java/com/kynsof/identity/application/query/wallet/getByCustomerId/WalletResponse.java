package com.kynsof.identity.application.query.wallet.getByCustomerId;


import com.kynsof.identity.domain.dto.WalletDto;
import com.kynsof.identity.domain.dto.WalletTransactionDto;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class WalletResponse implements IResponse {
    private UUID id;
    private BigDecimal balance;
    private List<WalletTransactionDto> transactions;

    public WalletResponse(WalletDto userSystemDto) {
        this.id = userSystemDto.getId();
        this.balance = userSystemDto.getBalance();
        this.transactions = userSystemDto.getTransactions();
    }

}