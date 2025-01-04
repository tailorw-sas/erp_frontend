package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageClientResponse;

import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageClient;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManagerClientWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageClientReadDataJPARepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagerClientServiceImpl implements IManagerClientService {

    @Autowired
    private ManagerClientWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageClientReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageClientDto dto) {
        ManageClient data = new ManageClient(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageClientDto dto) {
        ManageClient update = new ManageClient(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageClientDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageClientDto findById(UUID id) {
        Optional<ManageClient> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_CLIENT_NOT_FOUND,
                new ErrorField("id", "Manage Client not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageClient> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageClient> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageClient> data) {
        List<ManageClientResponse> responseList = new ArrayList<>();
        for (ManageClient entity : data.getContent()) {
            responseList.add(new ManageClientResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }


    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
