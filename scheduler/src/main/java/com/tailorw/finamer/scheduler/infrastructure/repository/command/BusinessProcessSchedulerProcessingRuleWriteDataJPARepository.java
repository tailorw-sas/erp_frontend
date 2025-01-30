package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerProcessingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessProcessSchedulerProcessingRuleWriteDataJPARepository extends JpaRepository<BusinessProcessSchedulerProcessingRule, UUID> {
}
