package com.kynsoft.finamer.settings.infrastructure.repository.command;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgencyType;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageRoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageRoomCategoryWriteDataJPARepository extends JpaRepository<ManageRoomCategory, UUID> {
}
