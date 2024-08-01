package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.AttachmentStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentStatusHistoryReadDataJPARepository extends JpaRepository<AttachmentStatusHistory, UUID>,
        JpaSpecificationExecutor<AttachmentStatusHistory> {

    Page<AttachmentStatusHistory> findAll(Specification specification, Pageable pageable);
}
