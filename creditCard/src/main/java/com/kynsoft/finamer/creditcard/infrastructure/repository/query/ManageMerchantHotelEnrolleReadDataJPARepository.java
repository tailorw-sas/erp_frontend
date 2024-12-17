package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchant;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchantHotelEnrolle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageMerchantHotelEnrolleReadDataJPARepository extends JpaRepository<ManageMerchantHotelEnrolle, UUID>,
        JpaSpecificationExecutor<ManageMerchantHotelEnrolle> {

    Page<ManageMerchantHotelEnrolle> findAll(Specification specification, Pageable pageable);

    @Query("SELECT e FROM ManageMerchantHotelEnrolle e WHERE e.manageMerchant = :manageMerchant AND e.manageHotel = :manageHotel")
    Optional<ManageMerchantHotelEnrolle> findByManageMerchantAndManageHotel(@Param("manageMerchant") ManageMerchant managerMerchant, @Param("manageHotel") ManageHotel managerHotel);

    @Query("SELECT e.manageHotel FROM ManageMerchantHotelEnrolle e WHERE e.manageMerchant = :manageMerchant AND e.manageHotel.isApplyByVcc = true AND e.status = 'ACTIVE' AND e.manageHotel.status = 'ACTIVE'")
    List<ManageHotel> findHotelsByManageMerchant(@Param("manageMerchant") ManageMerchant manageMerchant);

    @Query("SELECT COUNT(b) FROM ManageMerchantHotelEnrolle b WHERE b.manageMerchant.id = :managerMerchant AND b.manageHotel.id = :managerHotel AND b.id <> :id")
    Long countByManageMerchantAndManageHotelNotId(@Param("id") UUID id, @Param("managerMerchant") UUID managerMerchant, @Param("managerHotel") UUID managerHotel);
}
