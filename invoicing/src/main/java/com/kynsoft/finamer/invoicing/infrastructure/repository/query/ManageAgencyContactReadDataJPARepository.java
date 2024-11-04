package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgencyContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ManageAgencyContactReadDataJPARepository extends JpaRepository<ManageAgencyContact, UUID>,
        JpaSpecificationExecutor<ManageAgencyContact> {

    Page<ManageAgencyContact> findAll(Specification specification, Pageable pageable);
    @Query("SELECT mac FROM ManageAgencyContact mac JOIN mac.manageHotel h WHERE mac.manageAgency.id = :agencyId AND h.id = :hotelId")
    List<ManageAgencyContact> findByAgencyAndHotel(@Param("agencyId") UUID agencyId, @Param("hotelId") UUID hotelId);

}
