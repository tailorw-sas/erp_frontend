package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageTraidingCompaniesResponse;

import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageTradingCompanies;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageTradingCompaniesWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageTradingCompaniesReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageTradingCompaniesServiceImpl implements IManageTradingCompaniesService {

    @Autowired
    private final ManageTradingCompaniesWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageTradingCompaniesReadDataJPARepository repositoryQuery;

    public ManageTradingCompaniesServiceImpl(ManageTradingCompaniesWriteDataJPARepository repositoryCommand,
            ManageTradingCompaniesReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageTradingCompaniesDto dto) {
        ManageTradingCompanies entity = new ManageTradingCompanies(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageTradingCompaniesDto dto) {
        ManageTradingCompanies entity = new ManageTradingCompanies(dto);

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageTradingCompaniesDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageTradingCompaniesDto findById(UUID id) {
        Optional<ManageTradingCompanies> optionalEntity = repositoryQuery.findById(id);

        return optionalEntity.map(ManageTradingCompanies::toAggregate).orElse(null);

//        throw new BusinessNotFoundException(
//                new GlobalBusinessException(DomainErrorMessage.MANAGE_TRADING_COMPANIES_TYPE_NOT_FOUND,
//                        new ErrorField("id", "The source not found.")));
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManageTradingCompaniesDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageTradingCompanies::toAggregate).toList();
    }

    @Override
    public List<ManageTradingCompaniesDto> findAll() {
        return repositoryQuery.findAll().stream().map(ManageTradingCompanies::toAggregate).collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManageTradingCompanies> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageTradingCompanies> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageTradingCompanies> data) {
        List<ManageTraidingCompaniesResponse> responses = new ArrayList<>();
        for (ManageTradingCompanies p : data.getContent()) {
            responses.add(new ManageTraidingCompaniesResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
