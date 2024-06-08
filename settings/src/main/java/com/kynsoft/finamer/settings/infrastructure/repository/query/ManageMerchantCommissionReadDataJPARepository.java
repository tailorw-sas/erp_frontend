package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageMerchantCommission;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManageMerchantCommissionReadDataJPARepository extends JpaRepository<ManageMerchantCommission, UUID>,
        JpaSpecificationExecutor<ManageMerchantCommission> {

    Page<ManageMerchantCommission> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageMerchantCommission b WHERE b.managerMerchant.id = :managerMerchant AND b.manageCreditCartType.id = :manageCreditCartType AND b.deleted = false")
    Long countByManagerMerchantANDManagerCreditCartType(@Param("managerMerchant") UUID managerMerchant, @Param("manageCreditCartType") UUID manageCreditCartType);

    @Query("SELECT COUNT(b) FROM ManageMerchantCommission b WHERE b.managerMerchant.id = :managerMerchant AND b.manageCreditCartType.id = :manageCreditCartType AND b.deleted = false AND b.id <> :id")
    Long countByManagerMerchantANDManagerCurrencyIdNotId(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant, @Param("manageCreditCartType") UUID manageCreditCartType);

    @Query("SELECT COUNT(b) FROM ManageMerchantCommission b WHERE b.managerMerchant.id = :managerMerchant AND b.manageCreditCartType.id = :manageCreditCartType AND b.fromDate <= :toCheckDate AND b.toDate >= :fromCheckDate AND b.deleted = false AND b.id <> :id")
    Long countByManagerMerchantANDManagerCreditCartTypeANDDateRange(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant,
            @Param("manageCreditCartType") UUID manageCreditCartType,
            @Param("fromCheckDate") LocalDate fromCheckDate,
            @Param("toCheckDate") LocalDate toCheckDate);
}
