package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.Invoice;
import java.util.List;

import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface ManageInvoiceReadDataJPARepository extends JpaRepository<Invoice, UUID>, 
        JpaSpecificationExecutor<Invoice> {

    @Override
    Page<Invoice> findAll(Specification specification, Pageable pageable);

    List<Invoice> findByIdIn(List<UUID> ids);

    @EntityGraph(attributePaths = {"bookings"}, type = EntityGraph.EntityGraphType.FETCH)
    List<Invoice> findInvoiceWithEntityGraphByIdIn(List<UUID> ids);

    @Query("SELECT i FROM Invoice i " +
            "LEFT JOIN FETCH i.parent " +
            "LEFT JOIN FETCH i.bookings " +
            "LEFT JOIN FETCH i.hotel " +
            "LEFT JOIN FETCH i.agency " +
            "WHERE i.invoiceId IN :ids")
    List<Invoice> findInvoiceByInvoiceIdIn(@Param("ids") List<Long> ids);
}
