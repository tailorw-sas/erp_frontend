package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageBankAccount;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ManageBankAccountReadDataJPARepository extends JpaRepository<ManageBankAccount, UUID>,
        JpaSpecificationExecutor<ManageBankAccount> {

    Page<ManageBankAccount> findAll(Specification specification, Pageable pageable);

    Optional<ManageBankAccount> findManageBankAccountByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    @Query("Select pd from ManageBankAccount pd where pd.manageHotel.id=:hotelId")
    Optional<List<ManageBankAccount>> findAllByHotel(@Param("hotelId") UUID hotelId);
}
