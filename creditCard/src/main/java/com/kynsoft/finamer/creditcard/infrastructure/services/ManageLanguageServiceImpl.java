package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageLanguage;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageLanguageWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageLanguageReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageLanguageServiceImpl implements IManageLanguageService {

    @Autowired
    private final ManageLanguageWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageLanguageReadDataJPARepository repositoryQuery;

    public ManageLanguageServiceImpl(ManageLanguageWriteDataJPARepository repositoryCommand, ManageLanguageReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageLanguageDto dto) {
        ManageLanguage entity = new ManageLanguage(dto);
        ManageLanguage saved = repositoryCommand.save(entity);
        return saved.getId();
    }

    @Override
    public void update(ManageLanguageDto dto) {
        ManageLanguage entity = new ManageLanguage(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageLanguageDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageLanguageDto findById(UUID id) {
        Optional<ManageLanguage> optional = repositoryQuery.findById(id);

        if (optional.isPresent()) {
            return optional.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_LANGUAGE_NOT_FOUND, new ErrorField("id", "Manager language not found.")));
    }
}
