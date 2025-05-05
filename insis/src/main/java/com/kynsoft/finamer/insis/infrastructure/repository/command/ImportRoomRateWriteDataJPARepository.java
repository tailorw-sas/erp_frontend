package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ImportRoomRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportRoomRateWriteDataJPARepository extends JpaRepository<ImportRoomRate, UUID> {
}
