package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.ProcessingDateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingDateTypeWriteDataJPARepository extends JpaRepository<ProcessingDateType, UUID> {
}
