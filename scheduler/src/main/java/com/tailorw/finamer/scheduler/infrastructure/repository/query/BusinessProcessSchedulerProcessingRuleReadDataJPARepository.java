package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerProcessingRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BusinessProcessSchedulerProcessingRuleReadDataJPARepository extends JpaRepository<BusinessProcessSchedulerProcessingRule, UUID>, JpaSpecificationExecutor<BusinessProcessSchedulerProcessingRule> {
    Page<BusinessProcessSchedulerProcessingRule> findAll(Specification specification, Pageable pageable);
}
