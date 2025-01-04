package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageInvoiceTransactionTypeReadDataJPARepository extends JpaRepository<ManageInvoiceTransactionType, UUID>,
        JpaSpecificationExecutor<ManageInvoiceTransactionType> {

    Page<ManageInvoiceTransactionType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageInvoiceTransactionType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT t FROM ManageInvoiceTransactionType t WHERE t.defaults = true AND t.deleted = false")
    Optional<ManageInvoiceTransactionType> findByDefaults();
}
