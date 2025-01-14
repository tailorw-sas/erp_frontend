package com.tailorw.tcaInnsist.infrastructure.repository.command;

import com.tailorw.tcaInnsist.infrastructure.model.ManageConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ManageConnectionWriteDataJPARepository extends JpaRepository<ManageConnection, UUID> {
}
