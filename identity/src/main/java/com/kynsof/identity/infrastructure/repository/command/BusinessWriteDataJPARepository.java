package com.kynsof.identity.infrastructure.repository.command;


import com.kynsof.identity.infrastructure.identity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessWriteDataJPARepository extends JpaRepository<Business, UUID> {
}
