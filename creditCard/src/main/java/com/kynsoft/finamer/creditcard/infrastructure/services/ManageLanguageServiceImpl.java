package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageLanguageResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageLanguage;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageLanguage;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageLanguageWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageLanguageReadDataJPARepository;
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

    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageLanguage> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageLanguage> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageLanguage> data) {
        List<ManageLanguageResponse> responseList = new ArrayList<>();
        for (ManageLanguage entity : data.getContent()) {
            responseList.add(new ManageLanguageResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
