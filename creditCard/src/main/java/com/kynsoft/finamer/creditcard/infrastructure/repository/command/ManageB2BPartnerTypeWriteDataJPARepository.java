package com.kynsoft.finamer.creditcard.infrastructure.repository.command;


import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageB2BPartnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageB2BPartnerTypeWriteDataJPARepository extends JpaRepository<ManageB2BPartnerType, UUID> {

}
