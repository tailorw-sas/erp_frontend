package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessProcessWriteDataJPARepository extends JpaRepository<BusinessProcess, UUID> {
}
