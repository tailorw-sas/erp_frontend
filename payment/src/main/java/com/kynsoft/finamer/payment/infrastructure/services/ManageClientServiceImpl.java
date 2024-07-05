package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageClient;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageClientWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageClientReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManageClientServiceImpl implements IManageClientService {

    @Autowired
    private ManageClientReadDataJPARepository repositoryCommand;

    @Autowired
    private ManageClientWriteDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageClientDto dto) {
        ManageClient data = new ManageClient(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageClientDto dto) {
        ManageClient update = new ManageClient(dto);
        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageClientDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageClientDto findById(UUID id) {
        Optional<ManageClient> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_CLIENT_NOT_FOUND, new ErrorField("id", "Manage Client not found.")));
    }

}
