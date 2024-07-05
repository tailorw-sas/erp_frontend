package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAdjustment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageAdjustmentReadDataJPARepository extends JpaRepository<ManageAdjustment, UUID>,
        JpaSpecificationExecutor<ManageAdjustment> {

    Page<ManageAdjustment> findAll(Specification specification, Pageable pageable);


}
