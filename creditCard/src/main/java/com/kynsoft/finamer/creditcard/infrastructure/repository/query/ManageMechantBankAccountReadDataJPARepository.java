package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchantBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ManageMechantBankAccountReadDataJPARepository extends JpaRepository<ManageMerchantBankAccount, UUID>,
        JpaSpecificationExecutor<ManageMerchantBankAccount> {

    Page<ManageMerchantBankAccount> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageMerchantBankAccount b JOIN b.managerMerchant m " +
            "WHERE m.id = :managerMerchant AND b.manageBank.id = :manageBank " +
            "AND b.accountNumber = :accountNumber AND b.id <> :id")
    Long countByManagerMerchantANDManagerBankIdNotId(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant, @Param("manageBank") UUID manageBank, @Param("accountNumber") String accountNumber);

}
