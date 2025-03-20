package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.payment.domain.services.IManageLanguageService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageLanguage;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageLanguageWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageLanguageReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManageLanguageServiceImpl implements IManageLanguageService {

    private final ManageLanguageWriteDataJPARepository writeRepository;
    private final ManageLanguageReadDataJPARepository readRepository;

    public ManageLanguageServiceImpl(ManageLanguageWriteDataJPARepository writeRepository,
                                     ManageLanguageReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageLanguageDto dto) {
        ManageLanguage language = new ManageLanguage(dto);
        return writeRepository.save(language).getId();
    }

    @Override
    public void update(ManageLanguageDto dto) {
        ManageLanguage language = new ManageLanguage(dto);
        writeRepository.save(language);
    }

    @Override
    public ManageLanguageDto findById(UUID id) {
        Optional<ManageLanguage> language = readRepository.findById(id);
        if(language.isPresent()){
            return language.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_LANGUAGE_NOT_FOUND, new ErrorField("id", "Manager Language not found: " + id )));
    }

    @Override
    public ManageLanguageDto findByCode(String code) {
        return null;
    }
}
