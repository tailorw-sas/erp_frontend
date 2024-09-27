package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceStatusService {
    UUID create(ManageInvoiceStatusDto dto);

    void update(ManageInvoiceStatusDto dto);

    void delete(ManageInvoiceStatusDto dto);

    ManageInvoiceStatusDto findById(UUID id);

    ManageInvoiceStatusDto findByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageInvoiceStatusDto> findByIds(List<UUID> ids);

    ManageInvoiceStatusDto findByEInvoiceStatus(EInvoiceStatus invoiceStatus);
}
