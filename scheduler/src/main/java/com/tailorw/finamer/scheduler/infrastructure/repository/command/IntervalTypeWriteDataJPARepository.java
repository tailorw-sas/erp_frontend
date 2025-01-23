package com.tailorw.finamer.scheduler.infrastructure.repository.command;

import com.tailorw.finamer.scheduler.infrastructure.model.IntervalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IntervalTypeWriteDataJPARepository extends JpaRepository<IntervalType, UUID> {
}
