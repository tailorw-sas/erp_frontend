package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerExecutionRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BusinessProcessSchedulerExecutionRuleReadDataJPARepository extends JpaRepository<BusinessProcessSchedulerExecutionRule, UUID>, JpaSpecificationExecutor<BusinessProcessSchedulerExecutionRule> {
    Page<BusinessProcessSchedulerExecutionRule> findAll(Specification specification, Pageable pageable);
}
