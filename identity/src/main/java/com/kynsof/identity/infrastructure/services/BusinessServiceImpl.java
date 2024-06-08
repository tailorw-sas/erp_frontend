package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.business.search.BusinessResponse;
import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.identity.infrastructure.identity.Business;
import com.kynsof.identity.infrastructure.identity.ModuleSystem;
import com.kynsof.identity.infrastructure.repository.command.BusinessWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.BusinessModuleReadDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.BusinessReadDataJPARepository;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.utils.ConfigureTimeZone;
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
public class BusinessServiceImpl implements IBusinessService {

    @Autowired
    private BusinessWriteDataJPARepository repositoryCommand;

    @Autowired
    private BusinessReadDataJPARepository repositoryQuery;

    @Autowired
    private BusinessModuleReadDataJPARepository businessModuleReadDataJPARepository;

    @Override
    public void create(BusinessDto object) {
        this.repositoryCommand.save(new Business(object));
    }

    @Override
    public void update(BusinessDto objectDto) {        
        this.repositoryCommand.save(new Business(objectDto));
    }

    @Override
    public void delete(UUID id) {

        BusinessDto objectDelete = this.findById(id);
        objectDelete.setStatus(EBusinessStatus.INACTIVE);

        objectDelete.setDeleteAt(ConfigureTimeZone.getTimeZone());
        objectDelete.setDeleted(true);
        objectDelete.setName(objectDelete.getName() + "-" + UUID.randomUUID());
        objectDelete.setRuc(objectDelete.getRuc() + "-" + UUID.randomUUID());

        this.repositoryCommand.save(new Business(objectDelete));
    }

    @Override
    public BusinessDto getById(UUID id) {

        Optional<Business> object = this.repositoryQuery.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("id", "Business not found.")));
    }

   // @Cacheable(cacheNames = CacheConfig.BUSINESS_CACHE, unless = "#result == null")
    @Override
    public BusinessDto findById(UUID id) {
        Optional<Business> object = this.repositoryQuery.findById(id);
        if (object.isPresent()) {
            BusinessDto businessDto = object.get().toAggregate();

            List<ModuleSystem> moduleSystems = businessModuleReadDataJPARepository.findModulesByBusinessId(id);
            List<ModuleDto> moduleDtoList = moduleSystems.stream()
                    .map(moduleSystem -> new ModuleDto(
                                    moduleSystem.getId(),
                                    moduleSystem.getName(),
                                    moduleSystem.getImage(),
                                    moduleSystem.getDescription(),
                                    new ArrayList<>()
                            )
                    )
                    .collect(Collectors.toList());

            businessDto.setModuleDtoList(moduleDtoList);
            return businessDto;
        } else {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("Business.id", "Business not found.")));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCreteria(filterCriteria);
        GenericSpecificationsBuilder<Business> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Business> data = this.repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private void filterCreteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {
            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    EBusinessStatus enumValue = EBusinessStatus.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Empresa: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<Business> data) {
        List<BusinessResponse> patients = new ArrayList<>();
        for (Business o : data.getContent()) {
            patients.add(new BusinessResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByName(String name) {
        return repositoryQuery.countByName(name);
    }

    @Override
    public Long countByRuc(String ruc) {
        return repositoryQuery.countByRuc(ruc);
    }

    @Override
    public Long countByRucAndNotId(String ruc, UUID id) {
        return repositoryQuery.countByRucAndNotId(ruc, id);
    }

    @Override
    public Long countByNameAndNotId(String name, UUID id) {
        return repositoryQuery.countByNameAndNotId(name, id);
    }

}
