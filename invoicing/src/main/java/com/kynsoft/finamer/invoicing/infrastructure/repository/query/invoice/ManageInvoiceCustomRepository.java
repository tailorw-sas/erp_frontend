package com.kynsoft.finamer.invoicing.infrastructure.repository.query.invoice;

import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity.ManageInvoiceSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ManageInvoiceCustomRepository {
    Page<ManageInvoiceSearchProjection> findAllProjected(Specification<Invoice> specification, Pageable pageable);
}
