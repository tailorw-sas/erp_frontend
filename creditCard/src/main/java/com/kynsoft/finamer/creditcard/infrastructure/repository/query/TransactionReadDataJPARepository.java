package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.application.query.objectResponse.TransactionSearchResponse;
import com.kynsoft.finamer.creditcard.infrastructure.identity.Transaction;
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
public interface TransactionReadDataJPARepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    Page<Transaction> findAll(Specification specification, Pageable pageable);
    Optional<Transaction> findByTransactionUuid(UUID uuid);
   /* Page<TransactionSearchResponse> findAll(Specification specification, Pageable pageable);*/
    @Query("SELECT COUNT(r) FROM Transaction r WHERE r.reservationNumber = :reservationNumber AND r.hotel.id = :hotel")
    Long countByReservationNumberAndManageHotelIdAndNotId(@Param("reservationNumber") String reservationNumber, @Param("hotel") UUID hotel);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.parent.id = :parentId")
    Optional<Double> findSumOfAmountByParentId(@Param("parentId") Long parentId);
}
