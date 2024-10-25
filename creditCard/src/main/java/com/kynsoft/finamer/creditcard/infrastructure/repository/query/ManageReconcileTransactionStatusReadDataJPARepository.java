package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageReconcileTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ManageReconcileTransactionStatusReadDataJPARepository extends JpaRepository<ManageReconcileTransactionStatus, UUID>,
        JpaSpecificationExecutor<ManageReconcileTransactionStatus> {

    Page<ManageReconcileTransactionStatus> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageReconcileTransactionStatus b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

}
