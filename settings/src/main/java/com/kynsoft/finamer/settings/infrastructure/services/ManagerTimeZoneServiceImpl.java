package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerTimeZoneResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerTimeZoneService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerTimeZone;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerTimeZoneWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerTimeZoneReadDataJPARepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagerTimeZoneServiceImpl implements IManagerTimeZoneService {

    @Autowired
    private ManagerTimeZoneWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagerTimeZoneReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManagerTimeZoneDto dto) {
        ManagerTimeZone data = new ManagerTimeZone(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManagerTimeZoneDto dto) {
        ManagerTimeZone update = new ManagerTimeZone(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagerTimeZoneDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagerTimeZoneDto findById(UUID id) {
        Optional<ManagerTimeZone> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_TIME_ZONE_NOT_FOUND, new ErrorField("id", "Manager Time Zone not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagerTimeZone> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerTimeZone> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
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

    private PaginatedResponse getPaginatedResponse(Page<ManagerTimeZone> data) {
        List<ManagerTimeZoneResponse> userSystemsResponses = new ArrayList<>();
        for (ManagerTimeZone p : data.getContent()) {
            userSystemsResponses.add(new ManagerTimeZoneResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManagerTimeZoneDto> findAllToReplicate() {
        List<ManagerTimeZone> objects = this.repositoryQuery.findAll();
        List<ManagerTimeZoneDto> objectDtos = new ArrayList<>();

        for (ManagerTimeZone object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

}
