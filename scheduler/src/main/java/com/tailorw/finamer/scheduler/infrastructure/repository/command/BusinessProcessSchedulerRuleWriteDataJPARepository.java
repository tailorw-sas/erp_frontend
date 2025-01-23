package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessProcessSchedulerRuleWriteDataJPARepository extends JpaRepository<BusinessProcessSchedulerRule, UUID> {
}
