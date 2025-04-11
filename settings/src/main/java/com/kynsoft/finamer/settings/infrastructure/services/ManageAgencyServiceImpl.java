package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.settings.application.query.manageAgency.search.ManageAgencyListResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAgencyResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageAgencyWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageAgencyReadDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@EnableCaching
public class ManageAgencyServiceImpl implements IManageAgencyService {

    private final ManageAgencyWriteDataJPARepository repositoryCommand;
    private final ManageAgencyReadDataJPARepository repositoryQuery;

    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    public ManageAgencyServiceImpl(ManageAgencyWriteDataJPARepository repositoryCommand, 
                                   ManageAgencyReadDataJPARepository repositoryQuery,
                                   ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"manageAgency", "manageAgencyAll", "manageAgencyToReplicate"}, allEntries = true)
    public UUID create(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"manageAgency", "manageAgencyAll", "manageAgencyToReplicate"}, allEntries = true)
    public void update(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"manageAgency", "manageAgencyAll", "manageAgencyToReplicate"}, allEntries = true)
    public void delete(UUID id) {
        try {
            ManageAgency entity = this.repositoryQuery.findById(id)
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", "Entity not found."))));
            this.repositoryCommand.delete(entity);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
//    @Cacheable(cacheNames = "manageAgency", key = "#id", unless = "#result == null")
//    public ManageAgencyDto findById(UUID id) {
//        Optional<ManageAgency> optionalEntity = repositoryQuery.findById(id);
//        return optionalEntity.map(ManageAgency::toAggregate)
//                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND, new ErrorField("id", "The source not found."))));
//    }

    public ManageAgencyDto findById(UUID id) {
        long startTime = System.nanoTime();
        Optional<ManageAgency> optionalEntity = repositoryQuery.getAgencyByIdWithAllRelations(id);
        long endTime = System.nanoTime();
        Logger.getLogger(ManageAgencyServiceImpl.class.getName()).log(Level.WARNING, "Tiempo:" + (endTime - startTime)/1_000_000);

        return optionalEntity.map(ManageAgency::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND, new ErrorField("id", "The source not found."))));
    }

    @Override
//    @Cacheable(cacheNames = "manageAgencyAll", unless = "#result == null or #result.isEmpty()")
    public List<ManageAgencyDto> findAll() {
        return repositoryQuery.findAll().stream().map(ManageAgency::toAggregate).collect(Collectors.toList());
    }

    @Override
//    @Cacheable(cacheNames = "manageAgencyAll", unless = "#result == null")
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, UUID employeeId) {
        filterCriteria(filterCriteria, employeeId);
        GenericSpecificationsBuilder<ManageAgency> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageAgency> data = repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManageAgencyDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageAgency::toAggregate).toList();
    }

    @Override
    public Long countByDefaultAndNotId(UUID id) {
        return this.repositoryQuery.countByDefaultAndNotId(id);
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria, UUID employeeId) {
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
        List<UUID> agencyIds = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(employeeId);
        System.err.println("Agencias: " + agencyIds.size());
        //agencyIds.add(UUID.fromString("0ebacd4c-40b3-4cae-8f49-02a25207d3ee"));
        FilterCriteria fc = new FilterCriteria();
        fc.setKey("id");
        fc.setLogicalOperation(LogicalOperation.AND);
        fc.setOperator(SearchOperation.IN);
        fc.setValue(agencyIds);
        filterCriteria.add(fc);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAgency> data) {
        List<ManageAgencyListResponse> responseList = new ArrayList<>();
        for (ManageAgency entity : data.getContent()) {
            responseList.add(new ManageAgencyListResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(), data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
//    @Cacheable(cacheNames = "manageAgencyToReplicate", unless = "#result == null or #result.isEmpty()")
    public List<ManageAgencyDto> findAllToReplicate() {
        List<ManageAgency> objects = this.repositoryQuery.findAll();
        List<ManageAgencyDto> objectDtos = new ArrayList<>();
        for (ManageAgency object : objects) {
            objectDtos.add(object.toAggregate());
        }
        return objectDtos;
    }

}
