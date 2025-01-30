package com.kynsoft.finamer.payment.infrastructure.identity.projection;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BankAccountProjection {
    private UUID id;
    private String accountNumber;
    private String nameOfBank;
    private String status;

    public BankAccountProjection(UUID id, String accountNumber, String nameOfBank, String status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.nameOfBank = nameOfBank;
        this.status = status;
    }
    // getters...
}
