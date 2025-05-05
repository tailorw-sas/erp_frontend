package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;

public interface ManagePaymentTransactionTypeReadDataJPARepository extends JpaRepository<ManagePaymentTransactionType, UUID>,
        JpaSpecificationExecutor<ManagePaymentTransactionType> {

    Page<ManagePaymentTransactionType> findAll(Specification specification, Pageable pageable);

    Optional<ManagePaymentTransactionType> findByCode(String code);

    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.paymentInvoice = true")
    Optional<ManagePaymentTransactionType> findByPaymentInvoice();

    @EntityGraph(attributePaths = {"code"})
    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.paymentInvoice = true")
    Optional<ManagePaymentTransactionType> findByPaymentInvoiceEntityGraph();

    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.deposit = true")
    Optional<ManagePaymentTransactionType> findByDeposit();

    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.applyDeposit = true")
    Optional<ManagePaymentTransactionType> findByApplyDeposit();

    @EntityGraph(attributePaths = {"code"})
    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.applyDeposit = true")
    Optional<ManagePaymentTransactionType> findByApplyDepositEntityGraph();

    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.applyDeposit = true AND b.defaults = true")
    Optional<ManagePaymentTransactionType> findByApplyDepositAndDefaults();

    List<ManagePaymentTransactionType> findByCodeInOrPaymentInvoiceTrue(List<String> codes);

    @Query("SELECT b FROM ManagePaymentTransactionType b WHERE b.cash = true")
    Optional<ManagePaymentTransactionType> findByCashTue();
}
