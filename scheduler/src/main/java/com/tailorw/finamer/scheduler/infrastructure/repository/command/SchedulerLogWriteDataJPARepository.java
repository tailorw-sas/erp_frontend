package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.SchedulerLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SchedulerLogWriteDataJPARepository extends JpaRepository<SchedulerLog, UUID> {
}
