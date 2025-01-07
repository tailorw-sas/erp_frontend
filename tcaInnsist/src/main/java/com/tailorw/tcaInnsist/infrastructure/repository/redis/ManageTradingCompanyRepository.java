package com.tailorw.tcaInnsist.infrastructure.repository.redis;

import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageTradingCompany;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ManageTradingCompanyRepository extends CrudRepository<ManageTradingCompany, UUID> {
}
