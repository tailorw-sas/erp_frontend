package com.kynsoft.finamer.insis.infrastructure.repository.command;

import com.kynsoft.finamer.insis.infrastructure.model.ManageRoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageRoomCategoryWriteDataJPARepository extends JpaRepository<ManageRoomCategory, UUID> {
}
