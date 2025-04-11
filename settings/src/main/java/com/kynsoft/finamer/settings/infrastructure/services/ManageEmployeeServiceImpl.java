package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageEmployeeResponse;
import com.kynsoft.finamer.settings.application.query.userMe.UserMeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageEmployee;
import com.kynsoft.finamer.settings.infrastructure.projections.ManageEmployeeProjection;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageEmployeeWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository.ManageEmployeeCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageEmployeeServiceImpl implements IManageEmployeeService {

    private final ManageEmployeeWriteDataJPARepository repositoryCommand;

    private final ManageEmployeeReadDataJPARepository repositoryQuery;

    public ManageEmployeeServiceImpl(ManageEmployeeWriteDataJPARepository repositoryCommand, ManageEmployeeReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageEmployeeDto dto) {
        ManageEmployee data = new ManageEmployee(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageEmployeeDto dto) {
        ManageEmployee update = new ManageEmployee(dto);

        update.setUpdateAt(LocalDateTime.now());

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
    public ManageEmployeeDto findById(UUID id) {
        Optional<ManageEmployee> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND, new ErrorField("id", "Manage Employee not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageEmployee> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageEmployee> data = this.repositoryQuery.findAll(specifications, pageable);
        //Page<ManageEmployeeProjection> data = this.repositoryQuery.findAllCustom(specifications, pageable);

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
        List<ManageEmployeeResponse> userSystemsResponses = new ArrayList<>();
        for (ManageEmployee p : data.getContent()) {
            userSystemsResponses.add(new ManageEmployeeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

//    private PaginatedResponse getPaginatedResponse(Page<ManageEmployeeProjection> data) {
//        List<ManageEmployeeResponse> userSystemsResponses = new ArrayList<>();
//        for (ManageEmployeeProjection p : data.getContent()) {
//            userSystemsResponses.add(new ManageEmployeeResponse(p));
//        }
//        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
//                data.getTotalElements(), data.getSize(), data.getNumber());
//    }

    @Override
    public Long countByLoginNameAndNotId(String loginName, UUID id) {
        return this.repositoryQuery.countByLoginNameAndNotId(loginName, id);
    }

    @Override
    public Long countByEmailAndNotId(String email, UUID id) {
        return this.repositoryQuery.countByEmailAndNotId(email, id);
    }

    @Override
    public List<ManageEmployeeDto> finAllByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageEmployee::toAggregate).collect(Collectors.toList());
    }

    @Override
    public void saveAll(List<ManageEmployeeDto> dtos) {
        repositoryCommand.saveAll(dtos.stream().map(ManageEmployee::new).collect(Collectors.toList()));
    }

    @Override
    public List<ManageEmployeeDto> findAllToReplicate() {
        List<ManageEmployee> objects = this.repositoryQuery.findAll();
        List<ManageEmployeeDto> objectDtos = new ArrayList<>();

        for (ManageEmployee object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    @Override
    public UserMeResponse me(UUID id) {
        Optional<ManageEmployee> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            ManageEmployee data = userSystem.get();
            List<String> permissions = new ArrayList<>();
            data.getManagePermissionList().forEach(permission -> permissions.add(permission.getCode()));
            return new UserMeResponse(data.getId(),data.getLoginName(),
                   data.getEmail(), data.getFirstName(),
                   data.getLastName(), null, permissions);
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND, new ErrorField("id", "Manage Employee not found.")));
    }

}
