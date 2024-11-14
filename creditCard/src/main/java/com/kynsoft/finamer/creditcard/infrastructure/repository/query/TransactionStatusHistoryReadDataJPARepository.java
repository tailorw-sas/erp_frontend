package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.TransactionStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionStatusHistoryReadDataJPARepository extends JpaRepository<TransactionStatusHistory, UUID>,
        JpaSpecificationExecutor<TransactionStatusHistory> {

    Page<TransactionStatusHistory> findAll(Specification specification, Pageable pageable);
}
