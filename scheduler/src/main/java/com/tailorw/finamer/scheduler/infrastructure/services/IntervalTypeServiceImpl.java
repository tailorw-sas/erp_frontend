package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.IntervalTypeResponse;
import com.tailorw.finamer.scheduler.domain.dto.IntervalTypeDto;
import com.tailorw.finamer.scheduler.domain.services.IIntervalTypeService;
import com.tailorw.finamer.scheduler.infrastructure.model.IntervalType;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.IntervalTypeWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.IntervalTypeReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IntervalTypeServiceImpl implements IIntervalTypeService {

    private final IntervalTypeWriteDataJPARepository writeRepository;
    private final IntervalTypeReadDataJPARepository readRepository;

    public IntervalTypeServiceImpl(IntervalTypeWriteDataJPARepository writeRepository,
                                   IntervalTypeReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }


    @Override
    public UUID create(IntervalTypeDto dto) {
        IntervalType intervalType = new IntervalType(dto);
        return writeRepository.save(intervalType).getId();
    }

    @Override
    public void update(IntervalTypeDto dto) {
        IntervalType intervalType = new IntervalType(dto);
        intervalType.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(intervalType);
    }

    @Override
    public void delete(UUID id) {
        readRepository.findById(id).ifPresent(intervalType -> {
            intervalType.setStatus(Status.DELETED);
            intervalType.setDeletedAt(LocalDateTime.now());
            writeRepository.save(intervalType);
        });
    }

    @Override
    public IntervalTypeDto getById(UUID id) {
        Optional<IntervalType> intervalType = readRepository.findById(id);
        if(intervalType.isPresent()){
            return intervalType.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_INTERVAL_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_INTERVAL_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public IntervalTypeDto getByCode(String code) {
        return readRepository.findByCode(code).map(IntervalType::toAggregate).orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<IntervalType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<IntervalType> data = readRepository.findAll(specifications, pageable);
        List<IntervalTypeResponse> responses = data.stream()
                .map(intervalType -> {
                    return new IntervalTypeResponse(intervalType.toAggregate());
                })
                .toList();
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(), data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private void addDeletedFilter(List<FilterCriteria> filterCriteria){
        FilterCriteria deletedAtFilter = new FilterCriteria();
        /*deletedAtFilter.setKey("deletedAt");
        deletedAtFilter.setOperator(SearchOperation.EQUALS);
        deletedAtFilter.setValue(null);*/
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
