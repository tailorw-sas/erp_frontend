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
import com.kynsoft.finamer.settings.application.query.manageHotel.search.ManageHotelSearchResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageHotelWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageHotelReadDataJPARepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
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
public class ManageHotelServiceImpl implements IManageHotelService {

    private final ManageHotelWriteDataJPARepository repositoryCommand;
    private final ManageHotelReadDataJPARepository repositoryQuery;

    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    public ManageHotelServiceImpl(ManageHotelWriteDataJPARepository repositoryCommand, 
                                  ManageHotelReadDataJPARepository repositoryQuery,
                                  ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    @Override
//    @CacheEvict(cacheNames = {"manageHotel", "manageHotelAll", "manageHotelToReplicate"}, allEntries = true)
    public UUID create(ManageHotelDto dto) {
        ManageHotel entity = new ManageHotel(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
//    @CacheEvict(cacheNames = {"manageHotel", "manageHotelAll", "manageHotelToReplicate"}, allEntries = true)
    public void update(ManageHotelDto dto) {
        ManageHotel entity = new ManageHotel(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    @Override
//    @CacheEvict(cacheNames = "manageHotel", key = "#dto.id")
    public void delete(ManageHotelDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
//    @Cacheable(cacheNames = "manageHotel", key = "#id", unless = "#result == null")
    public ManageHotelDto findById(UUID id) {
        Optional<ManageHotel> optionalEntity = repositoryQuery.findManageHotelWithAllRelationsById(id);
        return optionalEntity.map(ManageHotel::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_HOTEL_NOT_FOUND, new ErrorField("id", "Manage Hotel not found."))));
    }

    @Override
//    @Cacheable(cacheNames = "manageHotelAll", unless = "#result == null or #result.isEmpty()")
    public List<ManageHotelDto> findAll() {
        return repositoryQuery.findAll().stream().map(ManageHotel::toAggregate).collect(Collectors.toList());
    }

    @Override
//    @Cacheable(cacheNames = "manageHotelAll", unless = "#result == null")
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, UUID employeeId) {
        filterCriteria(filterCriteria, employeeId);
        GenericSpecificationsBuilder<ManageHotel> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageHotel> data = repositoryQuery.findAllCustom(specifications, pageable);
        //Page<ManageHotel> data = repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
//    @Cacheable(cacheNames = "manageHotelByIds", key = "#ids")
    public List<ManageHotelDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageHotel::toAggregate).toList();
    }

    @Override
//    @Cacheable(cacheNames = "manageHotelToReplicate", unless = "#result == null or #result.isEmpty()")
    public List<ManageHotelDto> findAllToReplicate() {
        List<ManageHotel> objects = this.repositoryQuery.findAll();
        List<ManageHotelDto> objectDtos = new ArrayList<>();

        for (ManageHotel object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
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
        List<UUID> ids = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(employeeId);
        FilterCriteria fc = new FilterCriteria();
        fc.setKey("id");
        fc.setLogicalOperation(LogicalOperation.AND);
        fc.setOperator(SearchOperation.IN);
        fc.setValue(ids);
        filterCriteria.add(fc);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageHotel> data) {
        List<ManageHotelSearchResponse> responseList = new ArrayList<>();
        for (ManageHotel entity : data.getContent()) {
            responseList.add(new ManageHotelSearchResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(), data.getTotalElements(), data.getSize(), data.getNumber());
    }

}