package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BusinessProcessSchedulerRuleReadDataJPARepository extends JpaRepository<BusinessProcessSchedulerRule, UUID>, JpaSpecificationExecutor<BusinessProcessSchedulerRule> {
    Page<BusinessProcessSchedulerRule> findAll(Specification specification, Pageable pageable);
}
