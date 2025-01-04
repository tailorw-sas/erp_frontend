package com.kynsoft.finamer.invoicing.domain.services;



import com.kynsoft.finamer.invoicing.domain.dto.ManageBankAccountDto;

import java.util.UUID;

public interface IManageBankAccountService {

    UUID create(ManageBankAccountDto dto);

    void update(ManageBankAccountDto dto);

    void delete(ManageBankAccountDto dto);

    ManageBankAccountDto findById(UUID id);
    ManageBankAccountDto findByAccountNumber(String accountNumber);

    boolean existByAccountNumber(String accountNumber);
}
