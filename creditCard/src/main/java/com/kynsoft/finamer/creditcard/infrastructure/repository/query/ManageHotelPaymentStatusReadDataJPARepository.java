package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageHotelPaymentStatus;
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
public interface ManageHotelPaymentStatusReadDataJPARepository extends JpaRepository<ManageHotelPaymentStatus, UUID>,
        JpaSpecificationExecutor<ManageHotelPaymentStatus> {

    Page<ManageHotelPaymentStatus> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(t) FROM ManageHotelPaymentStatus t WHERE t.code = :code AND t.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("select count(t) from ManageHotelPaymentStatus t where t.inProgress = true and t.id <> :id")
    Long countByInProgressAndNotId(@Param("id") UUID id);

    @Query("select t from ManageHotelPaymentStatus t where t.inProgress = true and t.status = 'ACTIVE'")
    Optional<ManageHotelPaymentStatus> findByInProgress();

    @Query("select count(t) from ManageHotelPaymentStatus t where t.cancelled = true and t.id <> :id")
    Long countByCancelledAndNotId(@Param("id") UUID id);

    @Query("select t from ManageHotelPaymentStatus t where t.cancelled = true and t.status = 'ACTIVE'")
    Optional<ManageHotelPaymentStatus> findByCancelled();

    @Query("select count(t) from ManageHotelPaymentStatus t where t.completed = true and t.id <> :id")
    Long countByCompletedAndNotId(@Param("id") UUID id);

    @Query("select t from ManageHotelPaymentStatus t where t.completed = true and t.status = 'ACTIVE'")
    Optional<ManageHotelPaymentStatus> findByCompleted();

    @Query("select count(t) from ManageHotelPaymentStatus t where t.applied = true and t.id <> :id")
    Long countByAppliedAndNotId(@Param("id") UUID id);

    @Query("select t from ManageHotelPaymentStatus t where t.applied = true and t.status = 'ACTIVE'")
    Optional<ManageHotelPaymentStatus> findByApplied();
}
