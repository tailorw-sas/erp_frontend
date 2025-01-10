package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessSchedulerRuleResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerRuleDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerRuleService;
import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerRule;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.BusinessProcessSchedulerRuleWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.BusinessProcessSchedulerRuleReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BusinessProcessSchedulerRuleServiceImpl implements IBusinessProcessSchedulerRuleService {

    private final BusinessProcessSchedulerRuleWriteDataJPARepository writeRepository;
    private final BusinessProcessSchedulerRuleReadDataJPARepository readRepository;

    public BusinessProcessSchedulerRuleServiceImpl(BusinessProcessSchedulerRuleWriteDataJPARepository writeRepository,
                                                   BusinessProcessSchedulerRuleReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(BusinessProcessSchedulerRuleDto dto) {
        BusinessProcessSchedulerRule businessProcessSchedulerRule = new BusinessProcessSchedulerRule(dto);
        return writeRepository.save(businessProcessSchedulerRule).getId();
    }

    @Override
    public void update(BusinessProcessSchedulerRuleDto dto) {
        BusinessProcessSchedulerRule businessProcessSchedulerRule = new BusinessProcessSchedulerRule(dto);
        businessProcessSchedulerRule.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(businessProcessSchedulerRule);
    }

    @Override
    public void delete(UUID id) {
        readRepository.findById(id)
                .ifPresent(businessProcessSchedulerRule -> {
                    businessProcessSchedulerRule.setDeletedAt(LocalDateTime.now());
                    businessProcessSchedulerRule.setStatus(Status.DELETED);
                    writeRepository.save(businessProcessSchedulerRule);
                });
    }

    @Override
    public BusinessProcessSchedulerRuleDto getById(UUID id) {
        Optional<BusinessProcessSchedulerRule> rule = readRepository.findById(id);
        if(rule.isPresent()){
            return rule.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_RULE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_RULE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<BusinessProcessSchedulerRule> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<BusinessProcessSchedulerRule> data = readRepository.findAll(specifications, pageable);
        List<BusinessProcessSchedulerRuleResponse> responses = data.stream()
                .map(rule -> {
                    return new BusinessProcessSchedulerRuleResponse(rule.toAggregate());
                })
                .toList();
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(), data.getTotalElements(), data.getSize(), data.getNumber());

    }

    private void addDeletedFilter(List<FilterCriteria> filterCriteria){
        FilterCriteria deletedAtFilter = new FilterCriteria();
        deletedAtFilter.setKey("status");
        deletedAtFilter.setOperator(SearchOperation.NOT_EQUALS);
        deletedAtFilter.setValue(Status.DELETED);

        boolean hasDeletedAtFilter = filterCriteria.stream()
                .anyMatch(filter -> filter.getKey().equals("deletedAt"));
        if (!hasDeletedAtFilter) {
            filterCriteria.add(deletedAtFilter);
        }
    }
}
