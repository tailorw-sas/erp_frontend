package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoice;
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
public interface ManageInvoiceReadDataJPARepository extends JpaRepository<ManageInvoice, UUID>,
        JpaSpecificationExecutor<ManageInvoice> {

    Page<ManageInvoice> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageInvoice b WHERE b.invoiceNumber LIKE %:invoiceNumber%")
    Long findByInvoiceNumber( @Param("invoiceNumber") String invoiceNumber);

    @Query("SELECT SUM(t.invoiceAmount) FROM ManageInvoice t WHERE t.parent IS NOT NULL AND t.parent.id = :parentId AND t.invoiceType = 'CREDIT'")
    Optional<Double> findSumOfAmountByParentId(@Param("parentId") UUID parentId);
}
