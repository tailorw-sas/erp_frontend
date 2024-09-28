package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;

import java.util.UUID;

public interface IManageInvoiceTypeService {

    UUID create(ManageInvoiceTypeDto dto);

    void update(ManageInvoiceTypeDto dto);

    void delete(ManageInvoiceTypeDto dto);

    ManageInvoiceTypeDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);

    ManageInvoiceTypeDto findByEInvoiceType(EInvoiceType invoiceType);

    ManageInvoiceTypeDto findByCode(String code);
}
