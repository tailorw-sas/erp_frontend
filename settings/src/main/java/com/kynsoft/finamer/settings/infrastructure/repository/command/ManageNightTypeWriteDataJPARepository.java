package com.kynsoft.finamer.settings.infrastructure.repository.command;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageNightType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageNightTypeWriteDataJPARepository extends JpaRepository<ManageNightType, UUID> {

}
