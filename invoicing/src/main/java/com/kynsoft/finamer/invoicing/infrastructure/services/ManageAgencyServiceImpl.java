package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageAgencyWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageAgencyReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageAgencyServiceImpl implements IManageAgencyService {

    @Autowired
    private final ManageAgencyWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageAgencyReadDataJPARepository repositoryQuery;

    public ManageAgencyServiceImpl(ManageAgencyWriteDataJPARepository repositoryCommand, ManageAgencyReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageAgencyDto dto) {
        ManageAgency delete = new ManageAgency(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageAgencyDto findById(UUID id) {
        Optional<ManageAgency> optionalEntity = repositoryQuery.findById(id);

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
    public List<ManageAgencyDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageAgency::toAggregate).toList();
    }

    @Override
    public boolean existByCode(String manageHotelCode) {
        return repositoryQuery.existsManageAgenciesByCode(manageHotelCode);
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
