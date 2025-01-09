package com.tailorw.finamer.scheduler.infrastructure.repository.query;

import com.tailorw.finamer.scheduler.infrastructure.model.SchedulerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SchedulerLogReadDataJPARepository extends JpaRepository<SchedulerLog, UUID>, JpaSpecificationExecutor<SchedulerLog> {
}
