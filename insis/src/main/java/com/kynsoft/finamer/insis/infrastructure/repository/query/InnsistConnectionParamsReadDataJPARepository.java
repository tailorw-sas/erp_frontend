package com.kynsoft.finamer.insis.infrastructure.repository.query;

import com.kynsoft.finamer.insis.infrastructure.model.InnsistConnectionParams;
import com.kynsoft.finamer.insis.infrastructure.model.InnsistTcaConfigurationProperties;
import com.kynsoft.finamer.insis.infrastructure.model.ManageTradingCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InnsistConnectionParamsReadDataJPARepository extends JpaRepository<InnsistConnectionParams, UUID>, JpaSpecificationExecutor<InnsistConnectionParams> {

    Page<InnsistConnectionParams> findAll(Specification specification, Pageable pageable);

    @Query("SELECT connection_params " +
            "FROM InnsistConnectionParams connection_params " +
            "JOIN ManageTradingCompany trading_company ON trading_company.innsistConnectionParam.id = connection_params.id " +
            "WHERE trading_company.id = :tradingCompanyId")
    List<InnsistConnectionParams> findByTradingCompanyId(UUID tradingCompanyId);

    @Query("SELECT count(1) " +
            "FROM InnsistConnectionParams connection_params " +
            "JOIN ManageTradingCompany trading_company ON trading_company.innsistConnectionParam.id = connection_params.id " +
            "WHERE connection_params.id = :id")
    int hasTradingCompanyAssociation(UUID id);

    @Query("SELECT new com.kynsoft.finamer.insis.infrastructure.model.ManageTradingCompany(trading_company.id, trading_company.code, trading_company.company, trading_company.innsistCode, trading_company.status, trading_company.createdAt, trading_company.updatedAt, connection_params, trading_company.hasConnection) " +
            "FROM InnsistConnectionParams connection_params " +
            "JOIN ManageTradingCompany trading_company ON trading_company.innsistConnectionParam.id = connection_params.id " +
            "WHERE connection_params.id = :id")
    Optional<ManageTradingCompany> findTradingCompanyAssociated(UUID id);
}
