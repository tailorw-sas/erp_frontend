package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessSchedulerExecutionRuleResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerExecutionRuleDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerExecutionRuleService;
import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerExecutionRule;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.BusinessProcessSchedulerExecutionRuleWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.BusinessProcessSchedulerExecutionRuleReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BusinessProcessSchedulerExecutionRuleServiceImpl implements IBusinessProcessSchedulerExecutionRuleService {

    private final BusinessProcessSchedulerExecutionRuleWriteDataJPARepository writeRepository;
    private final BusinessProcessSchedulerExecutionRuleReadDataJPARepository readRepository;

    public BusinessProcessSchedulerExecutionRuleServiceImpl(BusinessProcessSchedulerExecutionRuleWriteDataJPARepository writeRepository,
                                                            BusinessProcessSchedulerExecutionRuleReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(BusinessProcessSchedulerExecutionRuleDto dto) {
        BusinessProcessSchedulerExecutionRule businessProcessSchedulerRule = new BusinessProcessSchedulerExecutionRule(dto);
        return writeRepository.save(businessProcessSchedulerRule).getId();
    }

    @Override
    public void update(BusinessProcessSchedulerExecutionRuleDto dto) {
        BusinessProcessSchedulerExecutionRule businessProcessSchedulerRule = new BusinessProcessSchedulerExecutionRule(dto);
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
    public BusinessProcessSchedulerExecutionRuleDto getById(UUID id) {
        Optional<BusinessProcessSchedulerExecutionRule> rule = readRepository.findById(id);
        if(rule.isPresent()){
            return rule.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_RULE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_RULE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<BusinessProcessSchedulerExecutionRule> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<BusinessProcessSchedulerExecutionRule> data = readRepository.findAll(specifications, pageable);
        List<BusinessProcessSchedulerExecutionRuleResponse> responses = data.stream()
                .map(rule -> {
                    return new BusinessProcessSchedulerExecutionRuleResponse(rule.toAggregate());
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
