package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMessageResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMessageDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerMessageService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerMessage;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerMessageWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerMessageReadDataJPARepository;
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
public class ManagerMessageServiceImpl implements IManagerMessageService {

    @Autowired
    private final ManagerMessageWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManagerMessageReadDataJPARepository repositoryQuery;

    public ManagerMessageServiceImpl(ManagerMessageWriteDataJPARepository repositoryCommand, ManagerMessageReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManagerMessageDto dto) {
        ManagerMessage entity = new ManagerMessage(dto);
        ManagerMessage saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManagerMessageDto dto) {
        ManagerMessage entity = new ManagerMessage(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManagerMessageDto dto) {
        ManagerMessage delete = new ManagerMessage(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManagerMessageDto findById(UUID id) {
        Optional<ManagerMessage> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("id", "The source not found.")));

    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagerMessage> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerMessage> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagerMessage> data) {
        List<ManagerMessageResponse> responseList = new ArrayList<>();
        for (ManagerMessage entity : data.getContent()) {
            responseList.add(new ManagerMessageResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
