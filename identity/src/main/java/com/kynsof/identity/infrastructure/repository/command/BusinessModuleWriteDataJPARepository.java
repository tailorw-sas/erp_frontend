package com.kynsof.identity.infrastructure.repository.command;

import com.kynsof.identity.infrastructure.identity.BusinessModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessModuleWriteDataJPARepository extends JpaRepository<BusinessModule, UUID> {
}
