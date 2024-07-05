package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageHotelWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageHotelReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageHotelServiceImpl implements IManageHotelService {

    @Autowired
    private final ManageHotelWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageHotelReadDataJPARepository repositoryQuery;

    public ManageHotelServiceImpl(ManageHotelWriteDataJPARepository repositoryCommand, ManageHotelReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageHotelDto dto) {
        ManageHotel entity = new ManageHotel(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageHotelDto dto) {
        ManageHotel entity = new ManageHotel(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageHotelDto dto) {
        ManageHotel delete = new ManageHotel(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageHotelDto findById(UUID id) {
        Optional<ManageHotel> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_HOTEL_NOT_FOUND, new ErrorField("id", "Manage Hotel not found.")));
    }

}
