package com.kynsof.identity.infrastructure.repository.query;

import com.kynsof.identity.infrastructure.identity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface WalletReadDataJPARepository extends JpaRepository<Wallet, UUID>, JpaSpecificationExecutor<Wallet> {

    Page<Wallet> findAll(Specification specification, Pageable pageable);
    @Query("SELECT b  FROM Wallet b WHERE b.customer.id = :customerId")
    Optional<Wallet> findByCustomerId(UUID customerId);
}
