package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRateReadDataJPARepository extends CrudRepository<RoomRate, UUID>, JpaSpecificationExecutor<RoomRate> {

    Optional<RoomRate> findByHotelAndInvoicingDateAndReservationCodeAndCouponNumberAndRenewalNumber(
            ManageHotel hotel,
            LocalDate invoicingDate,
            String reservationCode,
            String couponNumber,
            String renewalNumber
    );

    List<RoomRate> findByBooking_Id(UUID bookingId);

    Page<RoomRate> findAll(Specification specification, Pageable pageable);

    @Query(value = "SELECT * FROM get_rates_count_by_invoicedate(:hotelIds, :fromInvoiceDate, :toInvoiceDate)", nativeQuery = true)
    List<Object[]> countByHotelAndInvoiceDate(@Param("hotelIds") UUID[] hotelIds, @Param("fromInvoiceDate") LocalDate fromInvoiceDate, @Param("toInvoiceDate") LocalDate toInvoiceDate);
}
