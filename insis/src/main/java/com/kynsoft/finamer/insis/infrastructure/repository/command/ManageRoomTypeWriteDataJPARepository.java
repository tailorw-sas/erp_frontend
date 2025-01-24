package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageRoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageRoomTypeWriteDataJPARepository extends JpaRepository<ManageRoomType, UUID> {
}
