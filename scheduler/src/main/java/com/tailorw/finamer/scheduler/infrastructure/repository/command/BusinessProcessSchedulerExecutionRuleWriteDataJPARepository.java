package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerExecutionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessProcessSchedulerExecutionRuleWriteDataJPARepository extends JpaRepository<BusinessProcessSchedulerExecutionRule, UUID> {
}
