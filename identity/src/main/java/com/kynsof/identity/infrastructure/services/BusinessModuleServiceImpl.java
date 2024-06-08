package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.businessModule.search.BusinessModuleResponse;
import com.kynsof.identity.domain.dto.BusinessModuleDto;
import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessModuleService;
import com.kynsof.identity.infrastructure.identity.BusinessModule;
import com.kynsof.identity.infrastructure.identity.ModuleSystem;
import com.kynsof.identity.infrastructure.identity.Permission;
import com.kynsof.identity.infrastructure.repository.command.BusinessModuleWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.BusinessModuleReadDataJPARepository;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessModuleServiceImpl implements IBusinessModuleService {

    @Autowired
    private BusinessModuleWriteDataJPARepository commandRepository;

    @Autowired
    private BusinessModuleReadDataJPARepository queryRepository;

    @Override
    public List<BusinessModuleDto> findBusinessModuleByBusinessId(UUID businessId) {
        List<BusinessModule> businessModules = queryRepository.findByBusinessId(businessId);
        List<BusinessModuleDto> businessModulesDtos = new ArrayList<>();
        
        for (BusinessModule businessModule : businessModules) {
            businessModulesDtos.add(new BusinessModuleDto(
                    businessModule.getId(), 
                    businessModule.getBusiness().toAggregate(), 
                    businessModule.getModule().toAggregate())
            );
        }
        return businessModulesDtos;
    }

    @Override
    public List<ModuleDto> findModulesByBusinessId(UUID businessId) {
        List<BusinessModule> businessModules = queryRepository.findByBusinessId(businessId);

        return businessModules.stream().map(businessModule -> {
            ModuleSystem module = businessModule.getModule();
            List<PermissionDto> permissionsDto = new ArrayList<>();
            Set<Permission> permissions = module.getPermissions();
            permissions.forEach(permission -> permissionsDto.add(
                    new PermissionDto(
                            permission.getId(),
                            permission.getCode(),
                            permission.getDescription(),
                            null,
                            permission.getStatus(),
                            permission.getAction(),
                            permission.getCreatedAt()
                    )
            ));

            return new ModuleDto(module.getId(), module.getName(), module.getImage(), module.getDescription(), permissionsDto);
        }).collect(Collectors.toList());
    }

    @Override
    public void create(BusinessModuleDto object) {
        this.commandRepository.save(new BusinessModule(object));
    }

    @Override
    public void create(List<BusinessModuleDto> objects) {
        List<BusinessModule> businessModules = new ArrayList<>();
        for (BusinessModuleDto object : objects) {
            businessModules.add(new BusinessModule(object));
        }
        this.commandRepository.saveAll(businessModules);
    }

    @Override
    public void update(List<BusinessModuleDto> objects) {
        List<BusinessModule> businessModules = new ArrayList<>();
        for (BusinessModuleDto object : objects) {
            businessModules.add(new BusinessModule(object));
        }
        this.commandRepository.saveAll(businessModules);
    }

    @Override
    public void delete(BusinessModuleDto object) {
        BusinessModule delete = new BusinessModule(object);
        delete.setDeleted(Boolean.TRUE);

        this.commandRepository.save(delete);
    }

    @Override
    public void delete(List<BusinessModuleDto> deletes) {
        List<BusinessModule> deletesObject = new ArrayList<>();
        for (BusinessModuleDto businessModule : deletes) {
            BusinessModule d = new BusinessModule(businessModule);
            d.setDeleted(Boolean.TRUE);
            deletesObject.add(d);
        }
        this.commandRepository.saveAll(deletesObject);
    }

    @Override
    public BusinessModuleDto findById(UUID id) {

        Optional<BusinessModule> object = this.queryRepository.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_MODULE_NOT_FOUND, new ErrorField("id", "BusinessModule not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<BusinessModule> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<BusinessModule> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<BusinessModule> data) {
        List<BusinessModuleResponse> patients = new ArrayList<>();
        for (BusinessModule o : data.getContent()) {
            patients.add(new BusinessModuleResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByBussinessIdAndModuleId(UUID businessId, UUID moduleId) {
        return this.queryRepository.countByBussinessIdAndModuleId(businessId, moduleId);
    }

}
