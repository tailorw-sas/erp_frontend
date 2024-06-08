package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagerMerchantCurrency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagerMerchantCurrencyReadDataJPARepository extends JpaRepository<ManagerMerchantCurrency, UUID>,
        JpaSpecificationExecutor<ManagerMerchantCurrency> {

    Page<ManagerMerchantCurrency> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagerMerchantCurrency b WHERE b.managerMerchant.id = :managerMerchant AND b.managerCurrency.id = :managerCurrency AND b.deleted = false")
    Long countByCodeAndNotId(@Param("managerMerchant") UUID managerMerchant, @Param("managerCurrency") UUID managerCurrency);

    @Query("SELECT COUNT(b) FROM ManagerMerchantCurrency b WHERE b.managerMerchant.id = :managerMerchant AND b.managerCurrency.id = :managerCurrency AND b.deleted = false AND b.id <> :id")
    Long countByManagerMerchantANDManagerCurrencyIdNotId(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant, @Param("managerCurrency") UUID managerCurrency);

}
