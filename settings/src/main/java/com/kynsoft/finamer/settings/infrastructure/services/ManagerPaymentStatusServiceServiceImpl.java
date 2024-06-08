package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerPaymentStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerPaymentStatus;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerPaymentStatusWriteDataJpaRepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerPaymentStatusReadDataJpaRepository;
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
public class ManagerPaymentStatusServiceServiceImpl implements IManagerPaymentStatusService {

    @Autowired
    private ManagerPaymentStatusWriteDataJpaRepository repositoryCommand;

    @Autowired
    private ManagerPaymentStatusReadDataJpaRepository repositoryQuery;

    @Override
    public UUID create(ManagerPaymentStatusDto dto) {
        ManagerPaymentStatus entity = new ManagerPaymentStatus(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagerPaymentStatusDto dto) {
        ManagerPaymentStatus entity = new ManagerPaymentStatus(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManagerPaymentStatusDto dto) {
        ManagerPaymentStatus entity = new ManagerPaymentStatus(dto);
        entity.setDeleted(true);
        entity.setCode(entity.getCode() + "-" + UUID.randomUUID());
        entity.setName(entity.getCode() + "-" + UUID.randomUUID());
        entity.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public ManagerPaymentStatusDto findById(UUID id) {
        Optional<ManagerPaymentStatus> result = this.repositoryQuery.findById(id);
        if (result.isPresent()) {
            return result.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND, new ErrorField("code", "Payment Status code not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagerPaymentStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerPaymentStatus> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCode(String code, UUID id) {
        return repositoryQuery.countByCode(code, id);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagerPaymentStatus> data) {
        List<ManagerPaymentStatusResponse> userSystemsResponses = new ArrayList<>();
        for (ManagerPaymentStatus p : data.getContent()) {
            userSystemsResponses.add(new ManagerPaymentStatusResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
