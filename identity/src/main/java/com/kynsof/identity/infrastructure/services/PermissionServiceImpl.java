package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.permission.search.PermissionSearchResponse;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.identity.infrastructure.identity.Permission;
import com.kynsof.identity.infrastructure.repository.command.PermissionWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.PermissionReadDataJPARepository;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private PermissionWriteDataJPARepository writeRepository;

    @Autowired
    private PermissionReadDataJPARepository queryRepository;

    @Override
    public void create(PermissionDto dto) {
        this.writeRepository.save(new Permission(dto));
    }

    @Override
    public void update(PermissionDto objectDto) {
        this.writeRepository.save(new Permission(objectDto));
    }

    @Override
    public void delete(PermissionDto objectDto) {
        this.writeRepository.save(new Permission(objectDto));
    }

    @Override
    public void deleteAll(List<UUID> permissions) {
        List<Permission> delete = new ArrayList<>();
        for (UUID id : permissions) {
            try {
                PermissionDto permission = this.findById(id);

                Permission d = new Permission(permission);
                d.setDeleted(true);
                d.setCode(permission.getCode() + " + " + UUID.randomUUID());
                d.setStatus(PermissionStatusEnm.INACTIVE);

                delete.add(d);
            } catch (Exception e) {
                System.err.println("Permission not found!!!");
            }
        }
        this.writeRepository.saveAll(delete);
    }

    @Override
    public PermissionDto findById(UUID id) {
        Optional<Permission> object = this.queryRepository.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PERMISSION_NOT_FOUND, new ErrorField("id", "Permission not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCreteria(filterCriteria);
        GenericSpecificationsBuilder<Permission> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Permission> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private void filterCreteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {
            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    PermissionStatusEnm enumValue = PermissionStatusEnm.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Permisos: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<Permission> data) {
        List<PermissionSearchResponse> patients = new ArrayList<>();
        for (Permission o : data.getContent()) {
            patients.add(new PermissionSearchResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String name, UUID id) {
        return this.queryRepository.countByCodeAndNotId(name, id);
    }

}
