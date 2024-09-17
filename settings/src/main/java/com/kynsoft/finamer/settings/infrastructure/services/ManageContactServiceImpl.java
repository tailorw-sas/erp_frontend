package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageContactResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageContact;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageContactWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageContactReadDataJPARepository;
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
public class ManageContactServiceImpl implements IManageContactService {

    @Autowired
    private final ManageContactWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageContactReadDataJPARepository repositoryQuery;

    public ManageContactServiceImpl(ManageContactWriteDataJPARepository repositoryCommand, ManageContactReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageContactDto dto) {
        ManageContact entity = new ManageContact(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageContactDto dto) {
        ManageContact entity = new ManageContact(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageContactDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageContactDto findById(UUID id) {
        Optional<ManageContact> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_CONTACT_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageContact> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageContact> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndManageHotelIdAndNotId(String code, UUID manageHotelId, UUID id) {
        return repositoryQuery.countByCodeAndManageHotelIdAndNotId(code, manageHotelId, id);
    }

    @Override
    public Long countByEmailAndManageHotelIdAndNotId(String email, UUID manageHotelId, UUID id) {
        return repositoryQuery.countByEmailAndManageHotelIdAndNotId(email, manageHotelId, id);
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

    private PaginatedResponse getPaginatedResponse(Page<ManageContact> data) {
        List<ManageContactResponse> responseList = new ArrayList<>();
        for (ManageContact entity : data.getContent()) {
            responseList.add(new ManageContactResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<ManageContactDto> findAllToReplicate() {
        List<ManageContact> objects = this.repositoryQuery.findAll();
        List<ManageContactDto> objectDtos = new ArrayList<>();

        for (ManageContact object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

}
