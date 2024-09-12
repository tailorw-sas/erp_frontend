package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageB2BPartnerTypeResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageB2BPartnerTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageB2BPartnerType;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageB2BPartnerTypeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageB2BPartnerTypeReadDataJPARepository;
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
public class ManageB2BPartnerTypeServiceImpl implements IManageB2BPartnerTypeService {

    @Autowired
    private ManageB2BPartnerTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageB2BPartnerTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageB2BPartnerTypeDto dto) {
        ManageB2BPartnerType data = new ManageB2BPartnerType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageB2BPartnerTypeDto dto) {
        ManageB2BPartnerType update = new ManageB2BPartnerType(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageB2BPartnerTypeDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageB2BPartnerTypeDto findById(UUID id) {
        Optional<ManageB2BPartnerType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage B2BPartner Type not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageB2BPartnerType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageB2BPartnerType> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageB2BPartnerType> data) {
        List<ManageB2BPartnerTypeResponse> userSystemsResponses = new ArrayList<>();
        for (ManageB2BPartnerType p : data.getContent()) {
            userSystemsResponses.add(new ManageB2BPartnerTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
