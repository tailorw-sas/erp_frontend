package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageMerchantHotelEnrolle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagerMerchantHotelEnrolleReadDataJPARepository extends JpaRepository<ManageMerchantHotelEnrolle, UUID>,
        JpaSpecificationExecutor<ManageMerchantHotelEnrolle> {

    Page<ManageMerchantHotelEnrolle> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageMerchantHotelEnrolle b WHERE b.managerMerchant.id = :managerMerchant AND b.managerHotel.id = :managerHotel AND b.id <> :id")
    Long countByManageMerchantAndManageHotelNotId(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant, @Param("managerHotel") UUID managerHotel);

}
