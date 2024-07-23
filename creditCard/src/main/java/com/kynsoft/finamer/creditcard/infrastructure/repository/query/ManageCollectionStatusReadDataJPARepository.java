package com.kynsoft.finamer.creditcard.infrastructure.repository.query;

import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageCollectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManageCollectionStatusReadDataJPARepository extends JpaRepository<ManageCollectionStatus, UUID>,
        JpaSpecificationExecutor<ManageCollectionStatus> {

    Page<ManageCollectionStatus> findAll(Specification specification, Pageable pageable);

    Optional<ManageCollectionStatus> findByCode(String code);

    @Query("SELECT COUNT(b) FROM ManageCollectionStatus b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

    @Query("SELECT COUNT(b) FROM ManageCollectionStatus b WHERE b.name = :name AND b.id <> :id")
    Long countByNameAndNotId(@Param("name") String name, @Param("id") UUID id);
}
