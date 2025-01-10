package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingReadDataJPARepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {

    Optional<Booking> findByHotelAndInvoicingDateAndReservationCodeAndCouponNumber(
            ManageHotel hotel,
            LocalDate invoicingDate,
            String reservationCode,
            String couponNumber
    );

    Optional<Booking> findByHotelAndInvoicingDateAndReservationCodeAndCouponNumberAndStatus(
            ManageHotel hotel,
            LocalDate invoicingDate,
            String reservationCode,
            String couponNumber,
            BookingStatus status
    );

    Page<Booking> findAll(Specification specification, Pageable pageable);

    List<Booking> findByIdIn(List<UUID> ids);
}
