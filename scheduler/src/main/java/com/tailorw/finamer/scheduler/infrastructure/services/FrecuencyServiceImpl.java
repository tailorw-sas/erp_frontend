package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.tailorw.finamer.scheduler.application.query.objectResponse.FrequencyResponse;
import com.tailorw.finamer.scheduler.domain.dto.FrequencyDto;
import com.tailorw.finamer.scheduler.domain.services.IFrecuencyService;
import com.tailorw.finamer.scheduler.infrastructure.model.Frequency;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.FrequencyWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.FrequencyReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FrecuencyServiceImpl implements IFrecuencyService {

    private final FrequencyWriteDataJPARepository writeRepository;
    private final FrequencyReadDataJPARepository readRepository;

    public FrecuencyServiceImpl(FrequencyWriteDataJPARepository writeRepository, FrequencyReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(FrequencyDto dto) {
        Frequency frequency = new Frequency(dto);
        return writeRepository.save(frequency).getId();
    }

    @Override
    public void update(FrequencyDto dto) {
        Frequency frequency = new Frequency(dto);
        frequency.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(frequency);
    }

    @Override
    public void delete(UUID id) {
        readRepository.findById(id).ifPresent(frequency -> {
            frequency.setStatus(Status.DELETED);
            frequency.setDeletedAt(LocalDateTime.now());
            writeRepository.save(frequency);
        });
    }

    @Override
    public FrequencyDto getById(UUID id) {
        Optional<Frequency> frequency = readRepository.findById(id);
        if(frequency.isPresent()){
            return frequency.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_FRECUENCY_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_SCHEDULER_FRECUENCY_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public FrequencyDto getByCode(String code) {
        return readRepository.findByCode(code).map(Frequency::toAggregate).orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        addDeletedFilter(filterCriteria);
        GenericSpecificationsBuilder<Frequency> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Frequency> data = readRepository.findAll(specifications, pageable);
        List<FrequencyResponse> responses = data.stream()
                .map(frequency -> {
                    return new FrequencyResponse(frequency.toAggregate());
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
