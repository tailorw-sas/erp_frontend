package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageTradingCompaniesResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageTradingCompaniesService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageAttachmentType;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageTradingCompanies;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageTradingCompaniesWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageTradingCompaniesReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageTradingCompaniesServiceImpl implements IManageTradingCompaniesService {

    @Autowired
    private final ManageTradingCompaniesWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageTradingCompaniesReadDataJPARepository repositoryQuery;

    public ManageTradingCompaniesServiceImpl(ManageTradingCompaniesWriteDataJPARepository repositoryCommand, ManageTradingCompaniesReadDataJPARepository repositoryQuery) {
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

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageTradingCompaniesDto dto) {
        ManageTradingCompanies delete = new ManageTradingCompanies(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageTradingCompaniesDto findById(UUID id) {
        Optional<ManageTradingCompanies> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_TRADING_COMPANIES_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageTradingCompanies> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageTradingCompanies> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
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

    private PaginatedResponse getPaginatedResponse(Page<ManageTradingCompanies> data) {
        List<ManageTradingCompaniesResponse> responseList = new ArrayList<>();
        for (ManageTradingCompanies entity : data.getContent()) {
            responseList.add(new ManageTradingCompaniesResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
