package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.domain.dto.projection.ManageInvoiceSimpleProjection;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity.ManageInvoiceSearchProjection;
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
public interface ManageInvoiceReadDataJPARepository extends JpaRepository<Invoice, UUID>, 
        JpaSpecificationExecutor<Invoice> {

    Page<Invoice> findAll(Specification specification, Pageable pageable);
    @Query("""
    SELECT i.id AS id,
           i.invoiceId AS invoiceId,
           i.isManual AS isManual,
           i.invoiceNo AS invoiceNo,
           i.invoiceAmount AS invoiceAmount,
           i.dueAmount AS dueAmount,
           i.invoiceDate AS invoiceDate,
           h AS hotel,
           a AS agency,
           s AS invoiceStatus,
           i.hasAttachments AS hasAttachments,
           i.invoiceStatus AS status,
           i.invoiceType AS invoiceType,
           i.invoiceNumber AS invoiceNumber,
           t AS manageInvoiceType,
           i.sendStatusError AS sendStatusError,
           i.parent.id AS parent,
           i.autoRec AS autoRec,
           i.originalAmount AS originalAmount,
           i.importType AS importType,
           i.cloneParent AS cloneParent,
           i.aging AS aging
    FROM Invoice i
    LEFT JOIN i.hotel h
    LEFT JOIN i.agency a
    LEFT JOIN i.manageInvoiceStatus s
    LEFT JOIN i.manageInvoiceType t
   
""")
    Page<ManageInvoiceSearchProjection> findAllProjected(Specification<Invoice> specification, Pageable pageable);


//    @Query("SELECT m FROM Invoice m")
//    Page<ManageInvoiceSimpleProjection> findAllSimple(Specification<Invoice> specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Invoice b WHERE b.invoiceNumber LIKE %:invoiceNumber%")
    Long findByInvoiceNumber( @Param("invoiceNumber") String invoiceNumber);

    @Query("SELECT SUM(t.invoiceAmount) FROM Invoice t WHERE t.parent IS NOT NULL AND t.parent.id = :parentId AND t.invoiceType = 'CREDIT'")
    Optional<Double> findSumOfAmountByParentId(@Param("parentId") UUID parentId);

    Optional<Invoice> findByInvoiceId(long invoiceId);

    boolean existsByInvoiceId(long invoiceId);
}
