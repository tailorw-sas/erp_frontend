package com.kynsoft.notification.infrastructure.repository.command;

import com.kynsoft.notification.infrastructure.entity.AFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileWriteDataJPARepository extends JpaRepository<AFile, UUID> {
}
