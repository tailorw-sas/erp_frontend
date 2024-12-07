package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageBookingReadDataJPARepository extends JpaRepository<Booking, UUID>, 
        JpaSpecificationExecutor<Booking> {

    Page<Booking> findAll(Specification specification, Pageable pageable);

    //@Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM booking mb JOIN invoice  ON mb.manage_invoice=invoice.id  WHERE SPLIT_PART(TRIM(mb.hotelbookingnumber), ' ', 3) = :lastChars AND invoice.manage_hotel = :hotelId) THEN true ELSE false END", nativeQuery = true)
    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM booking mb JOIN invoice  ON mb.manage_invoice=invoice.id  WHERE SPLIT_PART(TRIM(mb.hotelbookingnumber), ' ', 3) = :lastChars AND invoice.manage_hotel = :hotelId AND invoice.invoiceStatus != 'CANCELED') THEN true ELSE false END", nativeQuery = true)
    boolean existsByExactLastChars(@Param("lastChars") String lastChars, @Param("hotelId") UUID hotelId);

    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM booking mb JOIN invoice  ON mb.manage_invoice=invoice.id  WHERE mb.hotelInvoiceNumber = :hotelInvoiceNumber AND invoice.manage_hotel = :hotelId AND invoice.invoiceStatus != 'CANCELED') THEN true ELSE false END", nativeQuery = true)
    boolean existsByHotelInvoiceNumber(@Param("hotelInvoiceNumber") String hotelInvoiceNumber, @Param("hotelId") UUID hotelId);

    boolean existsByHotelBookingNumber(String bookingNumber);

    Optional<Booking> findManageBookingByHotelBookingNumber(String hotelBookingNumber);

    @Query(value = "SELECT * " +
            "FROM booking mb " +
            "JOIN invoice ON mb.manage_invoice = invoice.id " +
            "WHERE invoice.id = :invoicingId", nativeQuery = true)
    List<Booking> findByManageInvoicing(@Param("invoicingId") UUID invoicingId);
}
