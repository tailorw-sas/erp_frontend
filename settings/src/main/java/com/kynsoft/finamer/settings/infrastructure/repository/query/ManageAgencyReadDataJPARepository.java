package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository.ManageAgencyCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageAgencyReadDataJPARepository extends JpaRepository<ManageAgency, UUID>,
        JpaSpecificationExecutor<ManageAgency>, ManageAgencyCustomRepository {

    Page<ManageAgency> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageAgency b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageAgency b WHERE b.isDefault = true AND b.id <> :id")
    Long countByDefaultAndNotId(@Param("id") UUID id);

    /*@Query("SELECT a FROM ManageAgency a " +
            "JOIN FETCH a.agencyType " +
            "JOIN FETCH a.client " +
            "JOIN FETCH a.sentB2BPartner " +
            "JOIN FETCH a.country " +
            "JOIN FETCH a.cityState " +
            "WHERE a.id = :id")
    Optional<ManageAgency> findByIdCustom(@Param("id") UUID id);*/
}
