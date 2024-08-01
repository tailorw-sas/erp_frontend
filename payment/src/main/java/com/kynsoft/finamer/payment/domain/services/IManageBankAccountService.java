package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageBankAccount;

import java.util.UUID;

public interface IManageBankAccountService {

    UUID create(ManageBankAccountDto dto);

    void update(ManageBankAccountDto dto);

    void delete(ManageBankAccountDto dto);

    ManageBankAccountDto findById(UUID id);
    ManageBankAccountDto findByAccountNumber(String accountNumber);

    boolean existByAccountNumber(String accountNumber);
}
