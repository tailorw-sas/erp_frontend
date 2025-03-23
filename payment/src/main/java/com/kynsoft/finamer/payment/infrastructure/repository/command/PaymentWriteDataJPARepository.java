package com.kynsoft.finamer.payment.infrastructure.repository.command;

import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PaymentWriteDataJPARepository extends JpaRepository<Payment, UUID> {

    @Modifying
    @Query("""
        UPDATE Payment p 
        SET p.paymentBalance = :paymentBalance,
            p.depositBalance = :depositBalance,
            p.identified = :identified,
            p.notIdentified = :notIdentified,
            p.notApplied = :notApplied,
            p.applied = :applied,
            p.applyPayment = :applyPayment
        WHERE p.id = :id
        """)
    @Transactional
    void updateBalances(
            @Param("paymentBalance") Double paymentBalance,
            @Param("depositBalance") Double depositBalance,
            @Param("identified") Double identified,
            @Param("notIdentified") Double notIdentified,
            @Param("notApplied") Double notApplied,
            @Param("applied") Double applied,
            @Param("applyPayment") boolean applyPayment,
            @Param("id") UUID id
    );
}
