package com.kynsoft.finamer.payment.infrastructure.repository.query.custom;

import com.kynsoft.finamer.payment.infrastructure.identity.Invoice;

import java.util.List;
import java.util.UUID;

public interface InvoiceCustomRepository {

    List<Invoice> findAllByIdInCustom(List<UUID> ids);
}
