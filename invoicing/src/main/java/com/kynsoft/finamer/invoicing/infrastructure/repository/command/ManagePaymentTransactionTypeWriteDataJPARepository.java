package com.kynsoft.finamer.invoicing.infrastructure.repository.command;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManagePaymentTransactionType;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManagePaymentTransactionTypeWriteDataJPARepository extends JpaRepository<ManagePaymentTransactionType, UUID> {

}
