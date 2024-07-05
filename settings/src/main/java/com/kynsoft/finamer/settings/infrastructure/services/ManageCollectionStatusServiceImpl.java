package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCollectionStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCollectionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageCollectionStatusService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageCollectionStatus;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageCollectionStatusWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageCollectionStatusReadDataJPARepository;
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
public class ManageCollectionStatusServiceImpl implements IManageCollectionStatusService {

    @Autowired
    private final ManageCollectionStatusWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageCollectionStatusReadDataJPARepository repositoryQuery;

    public ManageCollectionStatusServiceImpl(ManageCollectionStatusWriteDataJPARepository repositoryCommand, ManageCollectionStatusReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageCollectionStatusDto dto) {
        ManageCollectionStatus entity = new ManageCollectionStatus(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageCollectionStatusDto dto) {
        ManageCollectionStatus entity = new ManageCollectionStatus(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageCollectionStatusDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageCollectionStatusDto findById(UUID id) {
        Optional<ManageCollectionStatus> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Collection Status not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageCollectionStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageCollectionStatus> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManageCollectionStatusDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageCollectionStatus::toAggregate).toList();
    }

    @Override
    public Long countByNameAndNotId(String name, UUID id) {
        return repositoryQuery.countByNameAndNotId(name, id);
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

    private PaginatedResponse getPaginatedResponse(Page<ManageCollectionStatus> data) {
        List<ManageCollectionStatusResponse> responseList = new ArrayList<>();
        for (ManageCollectionStatus entity : data.getContent()) {
            responseList.add(new ManageCollectionStatusResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
