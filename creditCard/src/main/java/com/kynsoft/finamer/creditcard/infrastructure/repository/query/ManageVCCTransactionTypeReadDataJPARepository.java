
package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageVCCTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ManageVCCTransactionTypeReadDataJPARepository extends JpaRepository<ManageVCCTransactionType, UUID>,
        JpaSpecificationExecutor<ManageVCCTransactionType> {

    Page<ManageVCCTransactionType> findAll(Specification specification, Pageable pageable);

    Optional<ManageVCCTransactionType> findByCode(String code);

    @Query("SELECT t FROM ManageVCCTransactionType t WHERE t.isDefault = true AND t.subcategory = false AND t.status = 'ACTIVE'")
    Optional<ManageVCCTransactionType> findByIsDefaultAndNotIsSubCategory();

    @Query("SELECT t FROM ManageVCCTransactionType t WHERE t.isDefault = true AND t.subcategory = true AND t.status = 'ACTIVE'")
    Optional<ManageVCCTransactionType> findByIsDefaultAndIsSubCategory();

    @Query("SELECT t FROM ManageVCCTransactionType t WHERE t.manual = true AND t.status = 'ACTIVE'")
    Optional<ManageVCCTransactionType> findByManual();

    @Query("SELECT COUNT(b) FROM ManageVCCTransactionType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageVCCTransactionType b WHERE b.isDefault = true AND b.subcategory = false AND b.id <> :id")
    Long countByIsDefaultsAndNotSubCategoryAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageVCCTransactionType b WHERE b.isDefault = true AND b.subcategory = true AND b.id <> :id")
    Long countByIsDefaultsAndSubCategoryAndNotId(@Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageVCCTransactionType b WHERE b.manual = true AND b.id <> :id")
    Long countByManualAndNotId(@Param("id") UUID id);
}
