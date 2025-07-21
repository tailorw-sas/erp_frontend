package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import org.springframework.data.repository.query.Param;

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

    Page<RoomRate> findAll(Specification specification, Pageable pageable);

    @Query(value = "SELECT * FROM get_rates_count_by_invoicedate(:hotelIds, :fromInvoiceDate, :toInvoiceDate)", nativeQuery = true)
    List<Object[]> countByHotelAndInvoiceDate(@Param("hotelIds") UUID[] hotelIds, @Param("fromInvoiceDate") LocalDate fromInvoiceDate, @Param("toInvoiceDate") LocalDate toInvoiceDate);

    List<RoomRate> findByHotel_IdAndInvoicingDateAndStatusIn(UUID hotelId, LocalDate invoicingDate, List<RoomRateStatus> statuses);

    List<RoomRate> findByHotel_IdAndInvoicingDateAndStatusNot(UUID hotelId, LocalDate invoicingDate, RoomRateStatus status);

    @Query("SELECT r FROM RoomRate r WHERE r.id IN :ids AND r.status IN :statuses")
    List<RoomRate> findByIdsAndStatuses(@Param("ids") List<UUID> ids, @Param("statuses") List<RoomRateStatus> statuses);

    List<RoomRate> findByInvoiceId(UUID invoiceId);
}
