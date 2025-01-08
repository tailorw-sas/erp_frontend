package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessScheduler;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface BusinessProcessSchedulerReadDataJPARepository extends JpaRepository<BusinessProcessScheduler, UUID>, JpaSpecificationExecutor<BusinessProcessScheduler> {

    List<BusinessProcessScheduler> findByProcess_Id(UUID id);

    List<BusinessProcessScheduler> findByProcess_IdAndStatus(UUID id, Status status);

    List<BusinessProcessScheduler> findByProcess_IdAndStatusAndFrequency_Id(UUID processId, Status status, UUID frequencyId);

    Page<BusinessProcessScheduler> findAll(Specification specification, Pageable pageable);

}
