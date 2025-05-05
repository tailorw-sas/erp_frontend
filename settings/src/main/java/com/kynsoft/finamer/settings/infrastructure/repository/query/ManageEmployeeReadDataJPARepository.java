package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageEmployee;
import java.util.List;

import com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository.ManageEmployeeCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManageEmployeeReadDataJPARepository extends JpaRepository<ManageEmployee, UUID>,
        JpaSpecificationExecutor<ManageEmployee>, ManageEmployeeCustomRepository {

    Page<ManageEmployee> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageEmployee b WHERE b.loginName = :loginName AND b.id <> :id")
    Long countByLoginNameAndNotId(@Param("loginName") String loginName, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageEmployee b WHERE b.email = :email AND b.id <> :id")
    Long countByEmailAndNotId(@Param("email") String email, @Param("id") UUID id);

    @Query("SELECT agency.id FROM ManageEmployee e JOIN e.manageAgencyList agency WHERE e.id = :employeeId")
    List<UUID> findAgencyIdsByEmployeeId(@Param("employeeId") UUID employeeId);

    @Query("SELECT hotel.id FROM ManageEmployee e JOIN e.manageHotelList hotel WHERE e.id = :employeeId")
    List<UUID> findHotelsIdsByEmployeeId(@Param("employeeId") UUID employeeId);

    @Query("SELECT tCompany.id FROM ManageEmployee e JOIN e.manageTradingCompaniesList tCompany WHERE e.id = :employeeId")
    List<UUID> findManageTradingCompaniesIdsByEmployeeId(@Param("employeeId") UUID employeeId);
}
