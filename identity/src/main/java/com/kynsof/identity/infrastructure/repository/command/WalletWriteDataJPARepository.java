package com.kynsof.identity.infrastructure.repository.command;

import com.kynsof.identity.infrastructure.identity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletWriteDataJPARepository extends JpaRepository<Wallet, UUID> {
}
