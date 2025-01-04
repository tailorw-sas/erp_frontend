package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.manageAgencyContact.ManageAgencyContactResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyContactDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyContactService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgencyContact;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageAgencyContactWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageAgencyContactReadDataJPARepository;
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
public class ManageAgencyContactServiceImpl implements IManageAgencyContactService {

    private final ManageAgencyContactWriteDataJPARepository repositoryCommand;

    private final ManageAgencyContactReadDataJPARepository repositoryQuery;

    public ManageAgencyContactServiceImpl(ManageAgencyContactWriteDataJPARepository repositoryCommand,
                                          ManageAgencyContactReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public ManageAgencyContactDto create(ManageAgencyContactDto dto) {
        ManageAgencyContact entity = new ManageAgencyContact(dto);
        return this.repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void update(ManageAgencyContactDto dto) {
        ManageAgencyContact entity = new ManageAgencyContact(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageAgencyContactDto dto) {
        try{
            ManageAgencyContact entity = new ManageAgencyContact(dto);
            entity.setManageAgency(null);
            entity.setManageRegion(null);
            entity.setManageHotel(null);
            this.repositoryCommand.delete(entity);
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageAgencyContactDto findById(UUID id) {
        Optional<ManageAgencyContact> agencyContact = this.repositoryQuery.findById(id);
        if (agencyContact.isPresent()) {
            return agencyContact.get().toAggregate();
        }
        throw new BusinessNotFoundException(
                new GlobalBusinessException(
                        DomainErrorMessage.MANAGE_AGENCY_CONTACT_NOT_FOUND,
                        new ErrorField(
                                "id",
                                DomainErrorMessage.MANAGE_AGENCY_CONTACT_NOT_FOUND.getReasonPhrase()
                        )
                )
        );
    }
    @Override
    public List<ManageAgencyContact> findContactsByHotelAndAgency(UUID hotelId, UUID agencyId) {
        return repositoryQuery.findByAgencyAndHotel(agencyId, hotelId);
    }
    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageAgencyContact> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageAgencyContact> data = repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    @Override
    public List<ManageAgencyContactDto> findAllToReplicate() {
        List<ManageAgencyContact> objects = this.repositoryQuery.findAll();
        List<ManageAgencyContactDto> objectDtos = new ArrayList<>();

        for (ManageAgencyContact object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAgencyContact> data) {
        List<ManageAgencyContactResponse> responseList = new ArrayList<>();
        for (ManageAgencyContact entity : data.getContent()) {
            responseList.add(new ManageAgencyContactResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(), data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
