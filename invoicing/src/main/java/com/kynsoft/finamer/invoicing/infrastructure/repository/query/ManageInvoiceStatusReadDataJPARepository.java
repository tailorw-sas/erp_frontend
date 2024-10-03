package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageInvoiceStatusReadDataJPARepository extends JpaRepository<ManageInvoiceStatus, UUID>,
        JpaSpecificationExecutor<ManageInvoiceStatus> {

    Page<ManageInvoiceStatus> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageInvoiceStatus b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT b FROM ManageInvoiceStatus b WHERE b.code = :code")
    ManageInvoiceStatus findByCode(@Param("code") String code);

    @Query("SELECT COUNT(b) FROM ManageInvoiceStatus b WHERE b.sentStatus = true AND b.id <> :id")
    Long countBySentStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageInvoiceStatus b WHERE b.reconciledStatus = true AND b.id <> :id")
    Long countByReconciledStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageInvoiceStatus b WHERE b.canceledStatus = true AND b.id <> :id")
    Long countByCanceledStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageInvoiceStatus b WHERE b.processStatus = true AND b.id <> :id")
    Long countByProcessStatusAndNotId(@Param("id") UUID id);

}
