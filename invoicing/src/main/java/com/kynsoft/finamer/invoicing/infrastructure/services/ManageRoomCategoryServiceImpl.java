package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRoomCategoryResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomCategory;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageRoomCategoryWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRoomCategoryReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ManageRoomCategoryServiceImpl implements IManageRoomCategoryService {

    private final ManageRoomCategoryWriteDataJPARepository repositoryCommand;

    private final ManageRoomCategoryReadDataJPARepository repositoryQuery;

    public ManageRoomCategoryServiceImpl( ManageRoomCategoryWriteDataJPARepository repositoryCommand,
                                          ManageRoomCategoryReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageRoomCategoryDto dto) {
        ManageRoomCategory entity = new ManageRoomCategory(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageRoomCategoryDto dto) {
        ManageRoomCategory entity = new ManageRoomCategory(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageRoomCategoryDto dto) {
        ManageRoomCategory delete = new ManageRoomCategory(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageRoomCategoryDto findById(UUID id) {
        Optional<ManageRoomCategory> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));

    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageRoomCategory> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageRoomCategory> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageRoomCategory> data) {
        List<ManageRoomCategoryResponse> responseList = new ArrayList<>();
        for (ManageRoomCategory entity : data.getContent()) {
            responseList.add(new ManageRoomCategoryResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
