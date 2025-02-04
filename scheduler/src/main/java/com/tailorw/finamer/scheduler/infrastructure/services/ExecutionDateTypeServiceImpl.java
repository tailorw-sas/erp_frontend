package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.ExecutionDateTypeResponse;
import com.tailorw.finamer.scheduler.domain.dto.ExecutionDateTypeDto;
import com.tailorw.finamer.scheduler.domain.services.IExecutionDateTypeService;
import com.tailorw.finamer.scheduler.infrastructure.model.ExecutionDateType;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.ExecutionDateTypeWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.ExecutionDateTypeReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExecutionDateTypeServiceImpl implements IExecutionDateTypeService {

    private final ExecutionDateTypeWriteDataJPARepository writeRepository;
    private final ExecutionDateTypeReadDataJPARepository readRepository;

    public ExecutionDateTypeServiceImpl(ExecutionDateTypeWriteDataJPARepository writeRepositor, ExecutionDateTypeReadDataJPARepository readRepository){
        this.writeRepository = writeRepositor;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ExecutionDateTypeDto dto) {
        ExecutionDateType executionDateType = new ExecutionDateType(dto);
        return writeRepository.save(executionDateType).getId();
    }

    @Override
    public void update(ExecutionDateTypeDto dto) {
        ExecutionDateType executionDateType = new ExecutionDateType(dto);
        executionDateType.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(executionDateType);
    }

    @Override
    public void delete(UUID id) {
        readRepository.findById(id).ifPresent(executionDateType -> {
            executionDateType.setStatus(Status.DELETED);
            executionDateType.setDeletedAt(LocalDateTime.now());
            writeRepository.save(executionDateType);
        });
    }

    @Override
    public ExecutionDateTypeDto getById(UUID id) {
        Optional<ExecutionDateType> executionDateType = readRepository.findById(id);
        if(executionDateType.isPresent()){
            return executionDateType.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_DATE_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_EXECUTION_DATE_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public ExecutionDateTypeDto getByCode(String code) {
        return readRepository.findByCode(code).map(ExecutionDateType::toAggregate).orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<ExecutionDateType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ExecutionDateType> data = readRepository.findAll(specifications, pageable);
        List<ExecutionDateTypeResponse> responses = data.stream()
                .map(executionDateType -> {
                    return new ExecutionDateTypeResponse(executionDateType.toAggregate());
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
