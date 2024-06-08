package com.kynsof.identity.application.query.walletTransaction.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindWalletTransactionByIdQuery implements IQuery {

    private final UUID id;

    public FindWalletTransactionByIdQuery(UUID id) {
        this.id = id;
    }

}
