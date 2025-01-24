package com.tailorw.tcaInnsist.infrastructure.repository.command;

import com.tailorw.tcaInnsist.infrastructure.model.ManageTradingCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageTradingCompanyWriteDataJPARepository extends JpaRepository<ManageTradingCompany, UUID> {
}
