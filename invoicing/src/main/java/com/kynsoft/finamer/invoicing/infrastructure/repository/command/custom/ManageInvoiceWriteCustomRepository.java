package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;

public interface ManageInvoiceWriteCustomRepository {

    void insert(Invoice invoice);
}
