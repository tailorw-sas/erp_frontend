package com.kynsoft.finamer.creditcard.infrastructure.repository.query;


import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerMerchantConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ManagerMerchantConfigReadJpaRepository extends JpaRepository<ManagerMerchantConfig, UUID>, JpaSpecificationExecutor<ManagerMerchantConfig> {
    Page<ManagerMerchantConfig> findAll(Specification specification, Pageable pageable);

    @Query(value = "SELECT COUNT(b) FROM ManagerMerchant b WHERE b.managerMerchant.id = :managerMerchant",nativeQuery = true)
    Long countByManagerMerchantConfig(@Param("managerMerchant") UUID managerMerchant);

    @Query(value = "SELECT COUNT(b) FROM ManagerMerchant b WHERE b.managerMerchant.id = :managerMerchant AND b.id <> :id",nativeQuery = true)
    Long countByManagerMerchantConfigNotId(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant);
}
