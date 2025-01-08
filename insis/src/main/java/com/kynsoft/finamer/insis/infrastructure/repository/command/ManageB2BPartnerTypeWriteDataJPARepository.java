package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageB2BPartnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageB2BPartnerTypeWriteDataJPARepository extends JpaRepository<ManageB2BPartnerType, UUID> {
}
