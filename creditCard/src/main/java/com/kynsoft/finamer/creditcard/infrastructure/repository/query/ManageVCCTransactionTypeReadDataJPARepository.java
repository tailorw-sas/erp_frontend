
package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageVCCTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

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
}
