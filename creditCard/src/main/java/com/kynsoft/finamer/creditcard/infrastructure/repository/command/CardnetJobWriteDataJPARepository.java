package com.kynsoft.finamer.creditcard.infrastructure.repository.command;

import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardnetJobWriteDataJPARepository extends JpaRepository<CardnetJob, UUID> {
}