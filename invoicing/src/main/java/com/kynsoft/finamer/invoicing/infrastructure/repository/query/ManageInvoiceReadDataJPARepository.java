package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.domain.dto.projection.ManageInvoiceSimpleProjection;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity.ManageInvoiceSearchProjection;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.invoice.ManageInvoiceCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ManageInvoiceReadDataJPARepository extends JpaRepository<Invoice, UUID>, 
        JpaSpecificationExecutor<Invoice>, ManageInvoiceCustomRepository {

    Page<Invoice> findAll(Specification specification, Pageable pageable);

    @EntityGraph(attributePaths = {"manageInvoiceType","manageInvoiceStatus","hotel", "agency"})
    @Query("SELECT i FROM Invoice i WHERE i.id IN :invoiceIds")
    List<Invoice> findInvoicesWithoutBookings(@Param("invoiceIds") List<UUID> invoiceIds);

    @Query("SELECT COUNT(b) FROM Invoice b WHERE b.invoiceNumber LIKE %:invoiceNumber%")
    Long findByInvoiceNumber( @Param("invoiceNumber") String invoiceNumber);

    @Query("SELECT SUM(t.invoiceAmount) FROM Invoice t WHERE t.parent IS NOT NULL AND t.parent.id = :parentId AND t.invoiceType = 'CREDIT'")
    Optional<Double> findSumOfAmountByParentId(@Param("parentId") UUID parentId);

    Optional<Invoice> findByInvoiceId(long invoiceId);

    boolean existsByInvoiceId(long invoiceId);

    @Query("SELECT i FROM Invoice i" +
            " LEFT JOIN FETCH i.parent " +
            " LEFT JOIN FETCH i.manageInvoiceType " +
            " LEFT JOIN FETCH i.manageInvoiceStatus " +
            " LEFT JOIN FETCH i.hotel " +
            " LEFT JOIN FETCH i.agency " +
            " LEFT JOIN FETCH i.attachments " +
            " WHERE i.id = :id")
    Invoice findInvoiceByUUID(@Param("id") UUID id);

    @Query("SELECT DISTINCT b.invoice.id FROM Booking b WHERE b.id = :bookingId")
    UUID findInvoiceIdByBookingId(@Param("bookingId") UUID bookingId);

    @Query("SELECT DISTINCT i FROM Invoice i " +
            " JOIN FETCH i.bookings " +
            " WHERE i.id = :invoiceId ")
    Optional<Invoice> findInvoiceByBookingIdWithBookings(@Param("invoiceId") UUID invoiceId);

    @Query("SELECT DISTINCT i FROM Invoice i " +
            "JOIN FETCH i.bookings " +
            "WHERE i.id IN ( " +
            "SELECT DISTINCT b.invoice.id FROM Booking b WHERE b.id IN :bookingIds " +
            ") "
    )
    List<Invoice> findInvoicesByBookingIds(@Param("bookingIds") List<UUID> bookingIds);

}
