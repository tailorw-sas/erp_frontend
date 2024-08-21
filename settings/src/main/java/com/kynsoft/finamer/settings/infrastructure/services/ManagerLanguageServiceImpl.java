package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerLanguageResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerLanguage;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerLanguageWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerLanguageReadDataJPARepository;
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
public class ManagerLanguageServiceImpl implements IManagerLanguageService {

    @Autowired
    private final ManagerLanguageWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManagerLanguageReadDataJPARepository repositoryQuery;

    public ManagerLanguageServiceImpl(ManagerLanguageWriteDataJPARepository repositoryCommand, ManagerLanguageReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManagerLanguageDto dto) {
        ManagerLanguage entity = new ManagerLanguage(dto);
        ManagerLanguage saved = repositoryCommand.save(entity);
        return saved.getId();
    }

    @Override
    public void update(ManagerLanguageDto dto) {
        ManagerLanguage entity = new ManagerLanguage(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManagerLanguageDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagerLanguageDto findById(UUID id) {
        Optional<ManagerLanguage> optional = repositoryQuery.findById(id);

        if (optional.isPresent()) {
            return optional.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_LANGUAGE_NOT_FOUND, new ErrorField("id", "Manager language not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagerLanguage> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerLanguage> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public Long countByDefaultAndNotId(UUID id) {
        return this.repositoryQuery.countByDefaultAndNotId(id);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagerLanguage> data) {
        List<ManagerLanguageResponse> responseList = new ArrayList<>();
        for (ManagerLanguage entity : data.getContent()) {
            responseList.add(new ManagerLanguageResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
