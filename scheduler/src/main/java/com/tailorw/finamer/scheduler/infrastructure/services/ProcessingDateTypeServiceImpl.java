package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.ProcessingDateTypeResponse;
import com.tailorw.finamer.scheduler.domain.dto.ProcessingDateTypeDto;
import com.tailorw.finamer.scheduler.domain.services.IProcessingDateTypeService;
import com.tailorw.finamer.scheduler.infrastructure.model.ProcessingDateType;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.ProcessingDateTypeWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.ProcessingDateTypeReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcessingDateTypeServiceImpl implements IProcessingDateTypeService {

    private final ProcessingDateTypeWriteDataJPARepository writeRepository;
    private final ProcessingDateTypeReadDataJPARepository readRepository;

    public ProcessingDateTypeServiceImpl(ProcessingDateTypeWriteDataJPARepository writeRepository,
                                         ProcessingDateTypeReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ProcessingDateTypeDto dto) {
        ProcessingDateType processingDateType = new ProcessingDateType(dto);
        return writeRepository.save(processingDateType).getId();
    }

    @Override
    public void update(ProcessingDateTypeDto dto) {
        ProcessingDateType processingDateType = new ProcessingDateType(dto);
        processingDateType.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(processingDateType);
    }

    @Override
    public void delete(UUID id) {
        readRepository.findById(id).ifPresent(processingDateType -> {
            processingDateType.setStatus(Status.DELETED);
            processingDateType.setDeletedAt(LocalDateTime.now());
            writeRepository.save(processingDateType);
        });
    }

    @Override
    public ProcessingDateTypeDto getById(UUID id) {
        Optional<ProcessingDateType> processingDateType = readRepository.findById(id);
        if(processingDateType.isPresent()){
            return processingDateType.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_PROCESSING_DATE_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_PROCESSING_DATE_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public ProcessingDateTypeDto getByCode(String code) {
        return readRepository.findByCode(code).map(ProcessingDateType::toAggregate).orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<ProcessingDateType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ProcessingDateType> data = readRepository.findAll(specifications, pageable);
        List<ProcessingDateTypeResponse> responses = data.stream()
                .map(processingDateType -> {
                    return new ProcessingDateTypeResponse(processingDateType.toAggregate());
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
