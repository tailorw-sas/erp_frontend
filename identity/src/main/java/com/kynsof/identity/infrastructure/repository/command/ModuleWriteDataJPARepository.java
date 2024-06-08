package com.kynsof.identity.infrastructure.repository.command;

import com.kynsof.identity.infrastructure.identity.ModuleSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModuleWriteDataJPARepository extends JpaRepository<ModuleSystem, UUID> {
}
