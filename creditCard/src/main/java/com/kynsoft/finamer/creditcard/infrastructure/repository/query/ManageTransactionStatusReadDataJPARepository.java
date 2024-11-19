package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageTransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ManageTransactionStatusReadDataJPARepository extends JpaRepository<ManageTransactionStatus, UUID>,
        JpaSpecificationExecutor<ManageTransactionStatus> {

    Page<ManageTransactionStatus> findAll(Specification specification, Pageable pageable);

    Optional<ManageTransactionStatus> findByCode(String code);

    @Query("SELECT COUNT(b) FROM ManageTransactionStatus b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageTransactionStatus b WHERE b.sentStatus = true AND b.id <> :id")
    Long countBySentStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT b FROM ManageTransactionStatus b WHERE b.sentStatus = true AND b.status = 'ACTIVE'")
    Optional<ManageTransactionStatus> findBySentStatus();

    @Query("SELECT COUNT(b) FROM ManageTransactionStatus b WHERE b.refundStatus = true AND b.id <> :id")
    Long countByRefundStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT b FROM ManageTransactionStatus b WHERE b.refundStatus = true AND b.status = 'ACTIVE'")
    Optional<ManageTransactionStatus> findByRefundStatus();

    @Query("SELECT COUNT(b) FROM ManageTransactionStatus b WHERE b.receivedStatus = true AND b.id <> :id")
    Long countByReceivedStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT b FROM ManageTransactionStatus b WHERE b.receivedStatus = true AND b.status = 'ACTIVE'")
    Optional<ManageTransactionStatus> findByReceivedStatus();

    @Query("SELECT COUNT(b) FROM ManageTransactionStatus b WHERE b.cancelledStatus = true AND b.id <> :id")
    Long countByCancelledStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT b FROM ManageTransactionStatus b WHERE b.cancelledStatus = true AND b.status = 'ACTIVE'")
    Optional<ManageTransactionStatus> findByCancelledStatus();

    @Query("SELECT COUNT(b) FROM ManageTransactionStatus b WHERE b.declinedStatus = true AND b.id <> :id")
    Long countByDeclinedStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT b FROM ManageTransactionStatus b WHERE b.declinedStatus = true AND b.status = 'ACTIVE'")
    Optional<ManageTransactionStatus> findByDeclinedStatus();

    @Query("SELECT COUNT(b) FROM ManageTransactionStatus b WHERE b.reconciledStatus = true AND b.id <> :id")
    Long countByReconciledStatusAndNotId(@Param("id") UUID id);

    @Query("SELECT b FROM ManageTransactionStatus b WHERE b.reconciledStatus = true AND b.status = 'ACTIVE'")
    Optional<ManageTransactionStatus> findByReconciledStatus();
}
