package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceType;
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
public interface ManageInvoiceTypeReadDataJPARepository extends JpaRepository<ManageInvoiceType, UUID>,
        JpaSpecificationExecutor<ManageInvoiceType> {

    Page<ManageInvoiceType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageInvoiceType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT b FROM ManageInvoiceType b WHERE b.code = :code AND b.deleted = false")
    Optional<ManageInvoiceType> findByCode(String code);

    @Query("SELECT b FROM ManageInvoiceType b WHERE b.income = true AND b.deleted = false AND status = 'ACTIVE'")
    Optional<ManageInvoiceType> findByIncome();

    @Query("SELECT b FROM ManageInvoiceType b WHERE b.credit = true AND b.deleted = false AND status = 'ACTIVE'")
    Optional<ManageInvoiceType> findByCredit();

    @Query("SELECT b FROM ManageInvoiceType b WHERE b.invoice = true AND b.deleted = false AND status = 'ACTIVE'")
    Optional<ManageInvoiceType> findByInvoice();
}
