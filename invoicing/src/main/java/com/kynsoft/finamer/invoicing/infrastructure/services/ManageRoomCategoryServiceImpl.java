package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomCategory;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageRoomCategoryWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRoomCategoryReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
