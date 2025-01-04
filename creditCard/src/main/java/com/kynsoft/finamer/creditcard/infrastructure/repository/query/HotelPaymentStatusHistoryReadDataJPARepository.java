package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.HotelPaymentStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HotelPaymentStatusHistoryReadDataJPARepository extends JpaRepository<HotelPaymentStatusHistory, UUID>,
        JpaSpecificationExecutor<HotelPaymentStatusHistory> {

    Page<HotelPaymentStatusHistory> findAll(Specification specification, Pageable pageable);

    @Query("SELECT b FROM HotelPaymentStatusHistory b WHERE b.hotelPayment.id = :hotelPayment")
    List<HotelPaymentStatusHistory> findAllByHotelPaymentId(@Param("hotelPayment") UUID hotelPayment);
}
