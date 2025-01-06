package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.ExecutionDateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExecutionDateTypeWriteDataJPARepository extends JpaRepository<ExecutionDateType, UUID> {
}
