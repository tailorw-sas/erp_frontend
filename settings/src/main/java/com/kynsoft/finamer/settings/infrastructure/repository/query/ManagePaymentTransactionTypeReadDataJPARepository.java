package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagePaymentTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagePaymentTransactionTypeReadDataJPARepository extends JpaRepository<ManagePaymentTransactionType, UUID>,
        JpaSpecificationExecutor<ManagePaymentTransactionType> {

    Page<ManagePaymentTransactionType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT t FROM ManagePaymentTransactionType t WHERE t.applyDeposit = :applyDeposit AND t.deposit = :deposit ORDER BY CASE WHEN t.defaults = :defaults THEN 0 ELSE 1 END, t.id ASC")
    Page<ManagePaymentTransactionType> findWithApplyDepositAndDepositFalse(@Param("applyDeposit") Boolean applyDeposit, @Param("deposit") Boolean deposit, @Param("defaults") Boolean defaults, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagePaymentTransactionType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentTransactionType b WHERE b.defaults = true AND b.id <> :id")
    Long countByDefaultAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentTransactionType b WHERE b.incomeDefault = true AND b.id <> :id")
    Long countByIncomeDefaultAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentTransactionType b WHERE b.deposit = true AND b.id <> :id")
    Long countByDepositAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManagePaymentTransactionType b WHERE b.applyDeposit = true AND b.id <> :id")
    Long countByApplyDepositAndNotId(@Param("id") UUID id);
}
