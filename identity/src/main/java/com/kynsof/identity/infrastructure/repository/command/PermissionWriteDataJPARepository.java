package com.kynsof.identity.infrastructure.repository.command;

import com.kynsof.identity.infrastructure.identity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionWriteDataJPARepository extends JpaRepository<Permission, UUID> {
}
