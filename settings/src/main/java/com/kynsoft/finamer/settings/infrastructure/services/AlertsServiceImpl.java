package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAlertsResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAlertsDto;
import com.kynsoft.finamer.settings.domain.services.IAlertsService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageAlerts;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageAlertsWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageAlertReadDataJPARepository;
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
public class AlertsServiceImpl implements IAlertsService {

    @Autowired
    private ManageAlertsWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageAlertReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageAlertsDto dto) {
        ManageAlerts data = new ManageAlerts(dto);
        ManageAlerts alert = repositoryCommand.save(data);
        return alert.getId();
    }

    @Override
    public void update(ManageAlertsDto dto) {
        ManageAlerts data = new ManageAlerts(dto);
        data.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(data);
    }

    @Override
    public void delete(ManageAlertsDto dto) {
        ManageAlerts data = new ManageAlerts(dto);
        data.setDeleted(Boolean.TRUE);
        data.setCode(data.getCode() + "-" + UUID.randomUUID());
        data.setName(data.getName() + "-" + UUID.randomUUID());
        data.setDeletedAt(LocalDateTime.now());

        this.repositoryCommand.save(data);
    }

    @Override
    public ManageAlertsDto findById(UUID id) {
        Optional<ManageAlerts> alertObject = this.repositoryQuery.findById(id);
        if (alertObject.isPresent()) {
            return alertObject.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.ALERT_NOT_FOUND, new ErrorField("code", "Alert code not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageAlerts> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageAlerts> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCode(String code) {
        return this.repositoryQuery.countByCode(code);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAlerts> data) {
        List<ManageAlertsResponse> userSystemsResponses = new ArrayList<>();
        for (ManageAlerts p : data.getContent()) {
            userSystemsResponses.add(new ManageAlertsResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }    

}
