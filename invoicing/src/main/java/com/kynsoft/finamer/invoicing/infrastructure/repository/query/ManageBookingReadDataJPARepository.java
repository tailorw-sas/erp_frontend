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

    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM booking mb JOIN invoice  ON mb.manage_invoice=invoice.id  " +
            "WHERE mb.hotelbookingnumber = :lastChars AND invoice.manage_hotel = :hotelId AND invoice.invoiceStatus != 'CANCELED') " +
            "THEN true ELSE false END", nativeQuery = true)
    boolean existsByExactLastChars(@Param("lastChars") String lastChars, @Param("hotelId") UUID hotelId);

    @Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM booking mb JOIN invoice  ON mb.manage_invoice=invoice.id  " +
            "WHERE mb.hotelInvoiceNumber = :hotelInvoiceNumber AND invoice.manage_hotel = :hotelId AND invoice.invoiceStatus != 'CANCELED') " +
            "THEN true ELSE false END", nativeQuery = true)
    boolean existsByHotelInvoiceNumber(@Param("hotelInvoiceNumber") String hotelInvoiceNumber, @Param("hotelId") UUID hotelId);

    Optional<Booking> findByBookingId(Long bookingId);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.roomRates WHERE b.invoice.id IN :invoiceIds")
    List<Booking> findBookingsWithRoomRatesByInvoiceIds(@Param("invoiceIds") List<UUID> invoiceIds);

    /**
     * Busca combinaciones Hotel+BookingNumber existentes de forma optimizada
     * Retorna las claves de las combinaciones que SÍ existen en BD
     */
    @Query(value = """
    SELECT CONCAT(h.code, '|', mb.hotelbookingnumber) as combination_key 
        FROM booking mb 
        JOIN invoice inv ON mb.manage_invoice = inv.id
        JOIN hotel h ON inv.manage_hotel = h.id
    WHERE h.code IN :hotelCodes 
      AND mb.hotelbookingnumber IN :bookingNumbers
      AND inv.invoiceStatus != 'CANCELED'  """, nativeQuery = true)
    List<String> findExistingHotelBookingCombinations(@Param("hotelCodes") List<String> hotelCodes, @Param("bookingNumbers") List<String> bookingNumbers);

    /**
     * Busca combinaciones Hotel+InvoiceNumber existentes de forma optimizada
     * SOLO para hoteles virtuales. Retorna las claves de las combinaciones que SÍ existen en BD
     */
    @Query(value = """
    SELECT CONCAT(h.code, '|', mb.hotelInvoiceNumber) as combination_key 
        FROM booking mb 
        JOIN invoice inv ON mb.manage_invoice = inv.id  
        JOIN hotel h ON inv.manage_hotel = h.id
    WHERE h.code IN :hotelCodes 
        AND mb.hotelInvoiceNumber IN :invoiceNumbers
        AND h.isVirtual = true
        AND inv.invoiceStatus != 'CANCELED'
    """, nativeQuery = true)
    List<String> findExistingHotelInvoiceCombinations(@Param("hotelCodes") List<String> hotelCodes, @Param("invoiceNumbers") List<String> invoiceNumbers);

    /**
     * Query alternativa más específica que valida combinación exacta una por una
     * (Para casos donde necesites validación individual)
     */
    @Query(value = """
    SELECT CASE WHEN EXISTS (
        SELECT 1 FROM booking mb 
        JOIN invoice inv ON mb.manage_invoice = inv.id  
        JOIN hotel h ON inv.manage_hotel = h.id
        WHERE h.code = :hotelCode 
          AND mb.hotelbookingnumber = :bookingNumber
          AND inv.invoiceStatus != 'CANCELED'
    ) THEN true ELSE false END
    """, nativeQuery = true)
    boolean existsByHotelCodeAndBookingNumber(@Param("hotelCode") String hotelCode, @Param("bookingNumber") String bookingNumber);

    /**
     * Query específica para validar hotel invoice number en hoteles virtuales
     */
    @Query(value = """
    SELECT CASE WHEN EXISTS (
        SELECT 1 FROM booking mb 
        JOIN invoice inv ON mb.manage_invoice = inv.id  
        JOIN hotel h ON inv.manage_hotel = h.id
        WHERE h.code = :hotelCode 
          AND mb.hotelInvoiceNumber = :invoiceNumber
          AND h.isVirtual = true
          AND inv.invoiceStatus != 'CANCELED'
    ) THEN true ELSE false END
    """, nativeQuery = true)
    boolean existsByHotelCodeAndInvoiceNumber(@Param("hotelCode") String hotelCode, @Param("invoiceNumber") String invoiceNumber);

    /**
     * Query de estadísticas para debugging - cuenta duplicados por hotel
     */
    @Query(value = """
    SELECT h.code as hotel_code, 
           COUNT(DISTINCT mb.hotelbookingnumber) as unique_bookings,
           COUNT(mb.hotelbookingnumber) as total_bookings,
           COUNT(mb.hotelbookingnumber) - COUNT(DISTINCT mb.hotelbookingnumber) as duplicates
    FROM booking mb 
    JOIN invoice inv ON mb.manage_invoice = inv.id  
    JOIN hotel h ON inv.manage_hotel = h.id
    WHERE h.code IN :hotelCodes 
      AND inv.invoiceStatus != 'CANCELED'
    GROUP BY h.code
    """, nativeQuery = true)
    List<Object[]> getDuplicateStatsByHotel(@Param("hotelCodes") List<String> hotelCodes);
}
