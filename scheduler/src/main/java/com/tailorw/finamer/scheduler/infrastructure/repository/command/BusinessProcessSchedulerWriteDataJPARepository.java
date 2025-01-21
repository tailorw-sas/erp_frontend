package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessProcessSchedulerWriteDataJPARepository extends JpaRepository<BusinessProcessScheduler, UUID> {
}
