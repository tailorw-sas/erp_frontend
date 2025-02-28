package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessSchedulerResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcessScheduler;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.BusinessProcessSchedulerWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.BusinessProcessSchedulerReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BusinessProcessSchedulerServiceImpl implements IBusinessProcessSchedulerService {

    private final BusinessProcessSchedulerWriteDataJPARepository writeRepository;
    private final BusinessProcessSchedulerReadDataJPARepository readRepository;

    public BusinessProcessSchedulerServiceImpl(BusinessProcessSchedulerWriteDataJPARepository writeRepository,
                                               BusinessProcessSchedulerReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(BusinessProcessSchedulerDto dto) {
        BusinessProcessScheduler businessProcessScheduler = new BusinessProcessScheduler(dto);
        return writeRepository.save(businessProcessScheduler).getId();
    }

    @Override
    public void update(BusinessProcessSchedulerDto dto) {
        BusinessProcessScheduler businessProcessScheduler = new BusinessProcessScheduler(dto);
        businessProcessScheduler.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(businessProcessScheduler);
    }

    @Override
    public void delete(UUID id) {
        readRepository.findById(id).ifPresent(businessProcessScheduler -> {
            businessProcessScheduler.setDeletedAt(LocalDateTime.now());
            writeRepository.save(businessProcessScheduler);
        });
    }

    @Override
    public BusinessProcessSchedulerDto findById(UUID id) {
        Optional<BusinessProcessScheduler> businessProcessScheduler = readRepository.findById(id);
        if(businessProcessScheduler.isPresent()){
            return businessProcessScheduler.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public List<BusinessProcessSchedulerDto> findByProcessId(UUID id) {
        return readRepository.findByProcess_Id(id).stream()
                .map(BusinessProcessScheduler::toAggregate)
                .toList();
    }

    @Override
    public List<BusinessProcessSchedulerDto> findByProcessIdAndStatus(UUID processId, Status status) {
        return readRepository.findByProcess_IdAndStatus(processId, status)
                .stream()
                .map(BusinessProcessScheduler::toAggregate)
                .toList();
    }

    @Override
    public List<BusinessProcessSchedulerDto> findByProcessIdAndStatusAndFrequency(UUID processId, Status status, UUID frequencyId) {
        return readRepository.findByProcess_IdAndStatusAndFrequency_Id(processId, status, frequencyId)
                .stream()
                .map(BusinessProcessScheduler::toAggregate)
                .toList();
    }

    @Override
    public Long countActivesByProcessId(UUID id) {
        return 0L;
    }

    @Override
    public PaginatedResponse search(List<FilterCriteria> filterCriteria, Pageable pageable) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<BusinessProcessScheduler> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<BusinessProcessScheduler> data = readRepository.findAll(specifications, pageable);
        List<BusinessProcessSchedulerResponse> responses = data.stream()
                .map(businessProcessScheduler -> {
                    return new BusinessProcessSchedulerResponse(businessProcessScheduler.toAggregate());
                })
                .toList();
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(), data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private void addDeletedFilter(List<FilterCriteria> filterCriteria){
        if(filterCriteria == null){
            filterCriteria = new ArrayList<>();
        }

        boolean hasDeletedAtFilter = filterCriteria.stream()
                .anyMatch(filter -> filter.getKey().equals("status"));

        if (!hasDeletedAtFilter) {
            FilterCriteria deletedAtFilter = new FilterCriteria();
            deletedAtFilter.setKey("status");
            deletedAtFilter.setOperator(SearchOperation.NOT_EQUALS);
            deletedAtFilter.setValue(Status.DELETED);
            filterCriteria.add(deletedAtFilter);
        }
    }
}
