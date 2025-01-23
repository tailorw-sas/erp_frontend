package com.tailorw.tcaInnsist.infrastructure.repository.query;

import com.tailorw.tcaInnsist.infrastructure.model.ManageTradingCompany;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageTradingCompanyReadDataJPARepository extends JpaRepository<ManageTradingCompany, UUID> {
}
