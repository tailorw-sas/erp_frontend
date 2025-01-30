package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageRatePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageRatePlanWriteDataJPARepository extends JpaRepository<ManageRatePlan, UUID> {
}
