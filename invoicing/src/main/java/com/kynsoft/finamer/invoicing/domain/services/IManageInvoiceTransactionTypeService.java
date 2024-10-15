package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;

import java.util.UUID;

public interface IManageInvoiceTransactionTypeService {

    UUID create(ManageInvoiceTransactionTypeDto dto);

    void update(ManageInvoiceTransactionTypeDto dto);

    void delete(ManageInvoiceTransactionTypeDto dto);

    ManageInvoiceTransactionTypeDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);

    ManageInvoiceTransactionTypeDto findByDefaults();
}
