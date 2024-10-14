package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerCurrency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ManagerCurrencyReadDataJPARepository extends JpaRepository<ManagerCurrency, UUID>,
        JpaSpecificationExecutor<ManagerCurrency> {

    Page<ManagerCurrency> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagerCurrency b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

}
