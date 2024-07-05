package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageBankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageBankAccountReadDataJPARepository extends JpaRepository<ManageBankAccount, UUID>,
        JpaSpecificationExecutor<ManageBankAccount> {

    Page<ManageBankAccount> findAll(Specification specification, Pageable pageable);
}
