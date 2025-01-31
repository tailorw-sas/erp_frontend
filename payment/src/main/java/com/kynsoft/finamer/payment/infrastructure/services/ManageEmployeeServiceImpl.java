package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageEmployeeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageEmployee;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageEmployeeWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;

@Service
public class ManageEmployeeServiceImpl implements IManageEmployeeService {

    @Autowired
    private ManageEmployeeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageEmployeeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageEmployeeDto dto) {
        ManageEmployee data = new ManageEmployee(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageEmployeeDto dto) {
        ManageEmployee update = new ManageEmployee(dto);
        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageEmployeeDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    @Cacheable(cacheNames = "manageEmployee", key = "#id", unless = "#result == null")
    public ManageEmployeeDto findById(UUID id) {
        Optional<ManageEmployee> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageEmployee> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageEmployee> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    Status enumValue = Status.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageEmployee> data) {
        List<ManageEmployeeResponse> responses = new ArrayList<>();
        for (ManageEmployee p : data.getContent()) {
            responses.add(new ManageEmployeeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
