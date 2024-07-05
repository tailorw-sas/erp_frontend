package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface ManageTransactionStatusReadDataJPARepository extends JpaRepository<ManageTransactionStatus, UUID>,
        JpaSpecificationExecutor<ManageTransactionStatus> {

    Page<ManageTransactionStatus> findAll(Specification specification, Pageable pageable);
}
