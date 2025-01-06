package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageAgencyWriteDataJPARepository extends JpaRepository<ManageAgency, UUID> {
}
