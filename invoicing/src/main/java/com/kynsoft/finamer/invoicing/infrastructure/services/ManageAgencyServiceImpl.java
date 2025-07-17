package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAgencyResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageAgencyWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageAgencyReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ManageAgencyServiceImpl implements IManageAgencyService {

    @Autowired
    private final ManageAgencyWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageAgencyReadDataJPARepository repositoryQuery;

    public ManageAgencyServiceImpl(ManageAgencyWriteDataJPARepository repositoryCommand, ManageAgencyReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageAgencyDto dto) {
        ManageAgency delete = new ManageAgency(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageAgencyDto findById(UUID id) {
        Optional<ManageAgency> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND.getReasonPhrase())));

    }

    @Override
    @Cacheable(cacheNames = "manageAgency", key = "#code", unless = "#result == null")
    public ManageAgencyDto findByCode(String code) {
        return repositoryQuery.findManageAgenciesByCode(code).map(ManageAgency::toAggregate)
                .orElseThrow(()->  new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND, new ErrorField("code", DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND.getReasonPhrase()))));
    }

    @CacheEvict(allEntries = true, value = "manageAgency")
    @Override
    public void clearManageHotelCache() {
        //System.out.println("Clearing manageAgency cache");
    }

    @Override
    public Map<UUID, ManageAgencyDto> getMapById(List<UUID> agencyIds) {
        if(Objects.isNull(agencyIds) || agencyIds.isEmpty()){
            throw new IllegalArgumentException("The agency ID list is null or empty");
        }

        return this.findByIds(agencyIds).stream()
                .collect(Collectors.toMap(ManageAgencyDto::getId, mangeAgencyDto -> mangeAgencyDto));
    }

    @Override
    public Map<String, ManageAgencyDto> getMapByCode(List<String> agencyCodes) {
        if(Objects.isNull(agencyCodes)){
            throw new IllegalArgumentException("The agency code list must not be null");
        }
        List<ManageAgency> agencies = this.repositoryQuery.findByCodeIn(agencyCodes);
        return agencies.stream()
                .map(ManageAgency::toAggregate)
                .collect(Collectors.toMap(ManageAgencyDto::getCode, manageAgencyDto -> manageAgencyDto));
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
    public boolean existByCode(String manageAgencyCode) {
        return repositoryQuery.existsManageAgenciesByCode(manageAgencyCode);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManageAgency> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageAgency> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAgency> data) {
        List<ManageAgencyResponse> userSystemsResponses = new ArrayList<>();
        for (ManageAgency p : data.getContent()) {
            userSystemsResponses.add(new ManageAgencyResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
