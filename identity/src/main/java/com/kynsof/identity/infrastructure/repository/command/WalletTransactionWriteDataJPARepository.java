package com.kynsof.identity.infrastructure.repository.command;


import com.kynsof.identity.infrastructure.identity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletTransactionWriteDataJPARepository extends JpaRepository<WalletTransaction, UUID> {
}
