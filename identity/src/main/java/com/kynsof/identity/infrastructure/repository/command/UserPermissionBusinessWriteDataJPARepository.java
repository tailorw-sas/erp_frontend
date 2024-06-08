package com.kynsof.identity.infrastructure.repository.command;

import com.kynsof.identity.infrastructure.identity.UserPermissionBusiness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPermissionBusinessWriteDataJPARepository extends JpaRepository<UserPermissionBusiness, UUID> {
}
