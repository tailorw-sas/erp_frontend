package com.kynsoft.notification.infrastructure.repository.command;

import com.kynsoft.notification.infrastructure.entity.AdvertisingContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdvertisingContentWriteDataJPARepository extends JpaRepository<AdvertisingContent, UUID> {
}
