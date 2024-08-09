package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.DBConection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DBConectionWriteDataJPARepository extends JpaRepository<DBConection, UUID> {
}
