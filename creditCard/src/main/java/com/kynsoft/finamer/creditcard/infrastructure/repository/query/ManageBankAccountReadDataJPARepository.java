package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageBankAccountReadDataJPARepository extends JpaRepository<ManageBankAccount, UUID>,
        JpaSpecificationExecutor<ManageBankAccount> {

    Page<ManageBankAccount> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageBankAccount b WHERE b.accountNumber = :accountNumber AND b.id <> :id")
    Long countByAccountNumberAndNotId(@Param("accountNumber") String accountNumber, @Param("id") UUID id);
}
