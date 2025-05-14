package com.kynsoft.finamer.payment.infrastructure.repository.query;

import com.kynsoft.finamer.payment.infrastructure.identity.ManageEmployee;
import java.util.List;

import com.kynsoft.finamer.payment.infrastructure.repository.query.custom.ManageEmployeeCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManageEmployeeReadDataJPARepository extends JpaRepository<ManageEmployee, UUID>,
        JpaSpecificationExecutor<ManageEmployee>, ManageEmployeeCustomRepository {

    Page<ManageEmployee> findAll(Specification specification, Pageable pageable);

    @Query("SELECT agency.id FROM ManageEmployee e JOIN e.manageAgencyList agency WHERE e.id = :employeeId")
    List<UUID> findAgencyIdsByEmployeeId(@Param("employeeId") UUID employeeId);

    @Query("SELECT hotel.id FROM ManageEmployee e JOIN e.manageHotelList hotel WHERE e.id = :employeeId")
    List<UUID> findHotelsIdsByEmployeeId(@Param("employeeId") UUID employeeId);

    /*@Query("SELECT DISTINCT e " +
            "FROM ManageEmployee e " +
            "LEFT JOIN FETCH e.manageAgencyList " +
            "LEFT JOIN FETCH e.manageHotelList " +
            "WHERE e.id = :id ")
    Optional<ManageEmployee> findByIdCustom(@Param("id") UUID id);
*/
}
