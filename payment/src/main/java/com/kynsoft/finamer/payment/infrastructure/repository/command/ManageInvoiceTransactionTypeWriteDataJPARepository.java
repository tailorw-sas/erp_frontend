package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageInvoiceTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageInvoiceTransactionTypeWriteDataJPARepository extends JpaRepository<ManageInvoiceTransactionType, UUID> {
}
