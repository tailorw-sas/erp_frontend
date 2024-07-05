package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageVCCTransactionTypeService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageVCCTransactionType;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageVCCTransactionTypeWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageVCCTransactionTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageVCCTransactionTypeServiceImpl implements IManageVCCTransactionTypeService {

    @Autowired
    private ManageVCCTransactionTypeReadDataJPARepository repositoryQuery;

    @Autowired
    private ManageVCCTransactionTypeWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManageVCCTransactionTypeDto dto) {
        ManageVCCTransactionType entity = new ManageVCCTransactionType(dto);
        ManageVCCTransactionType saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManageVCCTransactionTypeDto dto) {
        repositoryCommand.save(new ManageVCCTransactionType(dto));
    }

    @Override
    public void delete(ManageVCCTransactionTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageVCCTransactionTypeDto findById(UUID id) {
        Optional<ManageVCCTransactionType> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("id", "The manager payment source not found.")));
    }

    @Override
    public List<ManageVCCTransactionTypeDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageVCCTransactionType::toAggregate).toList();
    }

    @Override
    public List<ManageVCCTransactionTypeDto> findAll() {
        return repositoryQuery.findAll().stream().map(ManageVCCTransactionType::toAggregate).collect(Collectors.toList());
    }
}
