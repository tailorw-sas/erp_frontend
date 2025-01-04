package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ParameterizationDto;
import com.kynsoft.finamer.invoicing.domain.services.IParameterizationService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Parameterization;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ParameterizationWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ParameterizationReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ParameterizationServiceImpl implements IParameterizationService {

    @Autowired
    private final ParameterizationWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ParameterizationReadDataJPARepository repositoryQuery;

    public ParameterizationServiceImpl(ParameterizationWriteDataJPARepository repositoryCommand, ParameterizationReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ParameterizationDto dto) {
        Parameterization entity = new Parameterization(dto);
        return this.repositoryCommand.save(entity).getId();
    }

    @Override
    public void delete(ParameterizationDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", "Element cannot be deleted")));
        }
    }

    @Override
    public ParameterizationDto findActiveParameterization() {
        Optional<Parameterization> optionalEntity = repositoryQuery.findActiveParameterization();

        return optionalEntity.map(Parameterization::toAggregate).orElse(null);
    }

    @Override
    public ParameterizationDto findById(UUID id) {
        Optional<Parameterization> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_PARAMETERIZATION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_PARAMETERIZATION_NOT_FOUND.getReasonPhrase())));
    }
}
