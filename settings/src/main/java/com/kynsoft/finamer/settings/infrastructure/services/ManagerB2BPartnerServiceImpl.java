package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerB2BPartnerResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageB2BPartner;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerB2BPartnerWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerB2BPartnerReadDataJPARepository;
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
public class ManagerB2BPartnerServiceImpl implements IManagerB2BPartnerService {

    @Autowired
    private ManagerB2BPartnerWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagerB2BPartnerReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManagerB2BPartnerDto dto) {
        ManageB2BPartner data = new ManageB2BPartner(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManagerB2BPartnerDto dto) {
        ManageB2BPartner update = new ManageB2BPartner(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagerB2BPartnerDto dto) {
        ManageB2BPartner delete = new ManageB2BPartner(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);
        delete.setDeleteAt(LocalDateTime.now());

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManagerB2BPartnerDto findById(UUID id) {
        Optional<ManageB2BPartner> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_B2BPARTNER_NOT_FOUND, new ErrorField("id", "Manager B2BPartner not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageB2BPartner> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageB2BPartner> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageB2BPartner> data) {
        List<ManagerB2BPartnerResponse> userSystemsResponses = new ArrayList<>();
        for (ManageB2BPartner p : data.getContent()) {
            userSystemsResponses.add(new ManagerB2BPartnerResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
