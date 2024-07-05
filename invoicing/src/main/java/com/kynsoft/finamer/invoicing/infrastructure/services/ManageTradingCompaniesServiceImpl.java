package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;

import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageTradingCompanies;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageTradingCompaniesWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageTradingCompaniesReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(
                new GlobalBusinessException(DomainErrorMessage.MANAGE_TRADING_COMPANIES_TYPE_NOT_FOUND,
                        new ErrorField("id", "The source not found.")));
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

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    Status enumValue = Status.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

}
