package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.Frequency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FrequencyWriteDataJPARepository extends JpaRepository<Frequency, UUID> {
}
