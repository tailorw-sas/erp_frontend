package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageAgencyService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageAgencyWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageAgencyReadDataJPARepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageAgencyServiceImpl implements IManageAgencyService {

    private final ManageAgencyWriteDataJPARepository repositoryCommand;
    private final ManageAgencyReadDataJPARepository repositoryQuery;

    public ManageAgencyServiceImpl(ManageAgencyWriteDataJPARepository repositoryCommand, ManageAgencyReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    @Transactional
    public UUID create(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    @Transactional
    public void update(ManageAgencyDto dto) {
        ManageAgency entity = new ManageAgency(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        try {
            ManageAgency entity = this.repositoryQuery.findById(id)
                    .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", "Entity not found."))));
            this.repositoryCommand.delete(entity);
            // Verificar si la entidad se ha eliminado correctamente
            if (repositoryCommand.existsById(id)) {
               String a = "aqio";
            }
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageAgencyDto findById(UUID id) {
        Optional<ManageAgency> optionalEntity = repositoryQuery.findById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    @Override
    public List<ManageAgencyDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageAgency::toAggregate).toList();
    }

    @Override
    public List<ManageAgencyDto> findAll() {
        return repositoryQuery.findAll().stream().map(ManageAgency::toAggregate).collect(Collectors.toList());
    }
}