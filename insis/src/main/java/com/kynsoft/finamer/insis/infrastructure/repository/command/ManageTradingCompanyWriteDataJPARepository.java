package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageTradingCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageTradingCompanyWriteDataJPARepository extends JpaRepository<ManageTradingCompany, UUID> {
}
