package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessResponse;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import com.tailorw.finamer.scheduler.infrastructure.model.BusinessProcess;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.BusinessProcessWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.BusinessProcessReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BusinessProcessServiceImpl implements IBusinessProcessService {

    private final BusinessProcessWriteDataJPARepository writeRepository;
    private final BusinessProcessReadDataJPARepository readRepository;

    public BusinessProcessServiceImpl(BusinessProcessWriteDataJPARepository writeRepository,
                                      BusinessProcessReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(BusinessProcessDto dto) {
        BusinessProcess businessProcess = new BusinessProcess(dto);
        return writeRepository.save(businessProcess).getId();
    }

    @Override
    public void update(BusinessProcessDto dto) {
        BusinessProcess businessProcess = new BusinessProcess(dto);
        writeRepository.save(businessProcess);
    }

    @Override
    public BusinessProcessDto findById(UUID id) {
        Optional<BusinessProcess> businessProcess = readRepository.findById(id);
        if(businessProcess.isPresent()){
            return businessProcess.get().toAgrregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BUSINESS_PROCESS_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public BusinessProcessDto findByCode(String code) {
        return readRepository.findByCode(code)
                .map(BusinessProcess::toAgrregate)
                .orElse(null);
    }

    @Override
    public BusinessProcessDto findByCodeAndByStatus(String code, String status) {
        return readRepository.findByCodeAndStatus(code, status).map(BusinessProcess::toAgrregate)
                .orElse(null);
    }

    @Override
    public long countActiveAndInactiveBusinessProcessSchedulers(UUID id) {
        return readRepository.countActiveAndInactiveBusinessProcessSchedulers(id);
    }

    @Override
    public PaginatedResponse search(List<FilterCriteria> filterCriteria, Pageable pageable) {
        GenericSpecificationsBuilder<BusinessProcess> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<BusinessProcess> data = readRepository.findAll(specifications, pageable);
        List<BusinessProcessResponse> businessProcessResponseList = data.stream()
                .map(businessProcess -> {
                    return new BusinessProcessResponse(businessProcess.toAgrregate());
                })
                .toList();

        return new PaginatedResponse(businessProcessResponseList, data.getTotalPages(), data.getNumberOfElements(), data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
