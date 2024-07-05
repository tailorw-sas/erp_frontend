package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageAttachmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageAttachmentTypeReadDataJPARepository extends JpaRepository<ManageAttachmentType, UUID>,
        JpaSpecificationExecutor<ManageAttachmentType> {

    Page<ManageAttachmentType> findAll(Specification specification, Pageable pageable);
}
