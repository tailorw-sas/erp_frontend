package com.kynsoft.finamer.audit.infrastructure.service;

import com.kynsoft.finamer.audit.application.query.configuration.getbyid.GetConfigurationByIdResponse;
import com.kynsoft.finamer.audit.application.service.AuditConfigurationService;
import com.kynsoft.finamer.audit.domain.dto.AuditConfigurationDto;
import com.kynsoft.finamer.audit.domain.exception.BusinessNotFoundException;
import com.kynsoft.finamer.audit.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.audit.domain.exception.GlobalBusinessException;
import com.kynsoft.finamer.audit.domain.request.FilterCriteria;
import com.kynsoft.finamer.audit.domain.response.ErrorField;
import com.kynsoft.finamer.audit.domain.response.PaginatedResponse;
import com.kynsoft.finamer.audit.infrastructure.identity.jpa.AuditConfiguration;
import com.kynsoft.finamer.audit.infrastructure.repository.AuditConfigurationRepository;
import com.kynsoft.finamer.audit.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuditConfigurationServiceImpl implements AuditConfigurationService {

    private final AuditConfigurationRepository auditConfigurationRepository;

    public AuditConfigurationServiceImpl(AuditConfigurationRepository auditConfigurationRepository) {
        this.auditConfigurationRepository = auditConfigurationRepository;
    }

    @Override
    public Optional<AuditConfigurationDto> findByServiceNameAndRegisterId(String serviceName, UUID registerId) {
        return auditConfigurationRepository.findAuditConfigurationByServiceNameAndId(serviceName,registerId).map(AuditConfiguration::toAggregate);
    }

    @Override
    public PaginatedResponse findAll(Pageable pageable) {
        return getPageResponse(auditConfigurationRepository.findAll(pageable));
    }

    @Override
    public GetConfigurationByIdResponse findById(UUID id) {
       Optional<AuditConfigurationDto> result= auditConfigurationRepository.findById(id).map(AuditConfiguration::toAggregate);

       if (result.isPresent()){
           AuditConfigurationDto auditConfigurationDto = result.get();
           return new GetConfigurationByIdResponse(auditConfigurationDto);
       }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id","The source not found")));

    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, String query) {
        GenericSpecificationsBuilder<AuditConfiguration> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<AuditConfiguration> data = auditConfigurationRepository.findAll(specifications, pageable);


        return getPageResponse(data);
    }

    @Override
    public void update(AuditConfigurationDto auditConfigurationDto) {
       Optional<AuditConfiguration> auditConfiguration = auditConfigurationRepository.findById(auditConfigurationDto.getId());
       auditConfiguration.ifPresent(auditConfiguration1 -> {
           auditConfiguration1.setAuditUpdate(auditConfigurationDto.isAuditUpdate());
           auditConfiguration1.setAuditDelete(auditConfigurationDto.isAuditDelete());
           auditConfiguration1.setAuditCreate(auditConfigurationDto.isAuditCreate());
           auditConfigurationRepository.save(auditConfiguration1);
       });

    }

    @Override
    public void registerServiceAudit(AuditConfigurationDto auditConfigurationDto) {
        AuditConfiguration auditConfiguration = new AuditConfiguration(auditConfigurationDto.getId(),auditConfigurationDto.isAuditUpdate(),
                auditConfigurationDto.isAuditCreate(),auditConfigurationDto.isAuditDelete(),auditConfigurationDto.getServiceName(),auditConfigurationDto.getEntityName());
        auditConfigurationRepository.save(auditConfiguration);
    }

    private PaginatedResponse getPageResponse(Page<AuditConfiguration> page){
        List<AuditConfigurationDto> content =page.getContent()
                .stream()
                .map(AuditConfiguration::toAggregate)
                .toList();
        return new PaginatedResponse(content,
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber());
    }
}
