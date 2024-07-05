package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchant;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchantHotelEnrolle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ManageMerchantHotelEnrolleReadDataJPARepository extends JpaRepository<ManageMerchantHotelEnrolle, UUID>,
        JpaSpecificationExecutor<ManageMerchantHotelEnrolle> {

    Page<ManageMerchantHotelEnrolle> findAll(Specification specification, Pageable pageable);

    Optional<ManageMerchantHotelEnrolle> findByManageMerchantAndManageHotel(ManageMerchant managerMerchant, ManageHotel managerHotel);

}
