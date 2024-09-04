package com.kynsoft.finamer.invoicing.infrastructure.repository.query;

import com.kynsoft.finamer.invoicing.infrastructure.identity.Parameterization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParameterizationReadDataJPARepository extends JpaRepository<Parameterization, UUID> {

    @Query(value = "FROM Parameterization t WHERE t.isActive = true")
    Optional<Parameterization> findActiveParameterization();
}
