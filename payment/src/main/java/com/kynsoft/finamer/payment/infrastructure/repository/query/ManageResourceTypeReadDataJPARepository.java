package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageResourceTypeReadDataJPARepository extends JpaRepository<ManageResourceType, UUID>,
        JpaSpecificationExecutor<ManageResourceType> {

    Page<ManageResourceType> findAll(Specification specification, Pageable pageable);
}
