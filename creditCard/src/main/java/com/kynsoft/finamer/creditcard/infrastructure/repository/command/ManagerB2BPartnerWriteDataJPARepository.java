package com.kynsoft.finamer.creditcard.infrastructure.repository.command;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageB2BPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManagerB2BPartnerWriteDataJPARepository extends JpaRepository<ManageB2BPartner, UUID> {

}
