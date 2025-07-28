package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.services.IManageEmployeeService;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageEmployeeResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageEmployee;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageEmployeeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageEmployeeDto findById(UUID id) {
        Optional<ManageEmployee> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND,
                new ErrorField("id", DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageEmployee> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageEmployee> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public String getEmployeeFullName(String employeeId) {
        Optional<ManageEmployee> employee = repositoryQuery.findByIdWithoutRelations(UUID.fromString(employeeId));
        if(employee.isPresent()){
            ManageEmployeeDto employeeDto = employee.get().toAggregate();
            return employeeDto.getFirstName() + " " + employeeDto.getLastName();
        }

        return employeeId;
    }

    @Override
    public List<ManageEmployeeDto> findAllByIdsWithoutRelations(List<UUID> ids) {
        if(Objects.isNull(ids) || ids.isEmpty()){
            throw new IllegalArgumentException("The employee ID list must not be null or empty");
        }

        return this.repositoryQuery.findAllByIdWithoutRelations(ids).stream()
                .map(ManageEmployee::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Map<UUID, String> getEmployeeFullNameMapByIds(List<UUID> ids) {
        if(Objects.isNull(ids) || ids.isEmpty()){
            throw new IllegalArgumentException("The employee ID list must not be null or empty");
        }

        return this.findAllByIdsWithoutRelations(ids).stream()
                .collect(Collectors.toMap(ManageEmployeeDto::getId,
                        manageEmployeeDto -> manageEmployeeDto.getFirstName() + " " + manageEmployeeDto.getLastName()));
    }

    @Override
    public List<UUID> findHotelsIdsByEmployeeId(String _employee_id) {
        if(Objects.isNull(_employee_id) || _employee_id.isEmpty()){
            throw new IllegalArgumentException("The employee ID must not be null or empty");
        }

        return repositoryQuery.findHotelsIdsByEmployeeId(UUID.fromString(_employee_id));
    }

    @Override
    public List<UUID> findAgencyIdsByEmployeeId(String _employee_id) {
        if(Objects.isNull(_employee_id) || _employee_id.isEmpty()){
            throw new IllegalArgumentException("The employee ID must not be null or empty");
        }

        return repositoryQuery.findAgencyIdsByEmployeeId(UUID.fromString(_employee_id));
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
