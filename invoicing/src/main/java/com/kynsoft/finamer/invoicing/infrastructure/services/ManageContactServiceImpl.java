package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageContactResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageContactDto;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageContact;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageContactWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageContactReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        GenericSpecificationsBuilder<ManageContact> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageContact> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageContact> data) {
        List<ManageContactResponse> responseList = new ArrayList<>();
        for (ManageContact entity : data.getContent()) {
            responseList.add(new ManageContactResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
