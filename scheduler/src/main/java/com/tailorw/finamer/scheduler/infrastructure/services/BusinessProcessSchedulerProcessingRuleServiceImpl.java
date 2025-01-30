package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessSchedulerProcessingRuleResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerProcessingRuleDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerProcessingRuleService;
import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessSchedulerProcessingRule;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.BusinessProcessSchedulerProcessingRuleWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.BusinessProcessSchedulerProcessingRuleReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BusinessProcessSchedulerProcessingRuleServiceImpl implements IBusinessProcessSchedulerProcessingRuleService {

    private final BusinessProcessSchedulerProcessingRuleWriteDataJPARepository writeRepository;
    private final BusinessProcessSchedulerProcessingRuleReadDataJPARepository readRepository;

    public BusinessProcessSchedulerProcessingRuleServiceImpl(BusinessProcessSchedulerProcessingRuleWriteDataJPARepository writeRepository,
                                                             BusinessProcessSchedulerProcessingRuleReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(BusinessProcessSchedulerProcessingRuleDto dto) {
        BusinessProcessSchedulerProcessingRule rule = new BusinessProcessSchedulerProcessingRule(dto);
        return writeRepository.save(rule).getId();
    }

    @Override
    public void update(BusinessProcessSchedulerProcessingRuleDto dto) {
        BusinessProcessSchedulerProcessingRule rule = new BusinessProcessSchedulerProcessingRule(dto);
        rule.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(rule);
    }

    @Override
    public void delete(UUID id) {
        readRepository.findById(id).ifPresent(businessProcessSchedulerProcessingRule -> {
            businessProcessSchedulerProcessingRule.setDeletedAt(LocalDateTime.now());
            businessProcessSchedulerProcessingRule.setStatus(Status.DELETED);
            writeRepository.save(businessProcessSchedulerProcessingRule);
        });
    }

    @Override
    public BusinessProcessSchedulerProcessingRuleDto getById(UUID id) {
        Optional<BusinessProcessSchedulerProcessingRule> rule = readRepository.findById(id);
        if(rule.isPresent()){
            return rule.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_PROCESSING_RULE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_PROCESSING_RULE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<BusinessProcessSchedulerProcessingRule> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<BusinessProcessSchedulerProcessingRule> data = readRepository.findAll(specifications, pageable);
        List<BusinessProcessSchedulerProcessingRuleResponse> responses = data.stream()
                .map(rule -> {
                    return new BusinessProcessSchedulerProcessingRuleResponse(rule.toAggregate());
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
