package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomType;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageRoomTypeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRoomTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageRoomTypeServiceImpl implements IManageRoomTypeService {

    @Autowired
    private final ManageRoomTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageRoomTypeReadDataJPARepository repositoryQuery;

    public ManageRoomTypeServiceImpl(ManageRoomTypeWriteDataJPARepository repositoryCommand, ManageRoomTypeReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageRoomTypeDto dto) {
        ManageRoomType entity = new ManageRoomType(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageRoomTypeDto dto) {
        ManageRoomType entity = new ManageRoomType(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageRoomTypeDto dto) {
        ManageRoomType delete = new ManageRoomType(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());

        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageRoomTypeDto findById(UUID id) {
        Optional<ManageRoomType> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Room type not found.")));
    }

    @Override
    public ManageRoomTypeDto findByCode(String code) {
        Optional<ManageRoomType> optionalEntity = repositoryQuery.findManageRoomTypeByCode(code);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("code", "Room type not found.")));
    }

    @Override
    public boolean existByCode(String code) {
        return repositoryQuery.existsManageRoomTypeByCode(code);
    }


    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public Long countByCodeAndManageHotelIdAndNotId(String code, UUID manageHotelId, UUID id) {
        return repositoryQuery.countByCodeAndManageHotelIdAndNotId(code,  id);
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
