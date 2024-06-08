package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.userPermissionBusiness.getbyid.UserRoleBusinessResponse;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.identity.infrastructure.identity.Permission;
import com.kynsof.identity.infrastructure.identity.UserPermissionBusiness;
import com.kynsof.identity.infrastructure.repository.command.UserPermissionBusinessWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.UserPermissionBusinessReadDataJPARepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPermissionBusinessServiceImpl implements IUserPermissionBusinessService {

    @Autowired
    private UserPermissionBusinessWriteDataJPARepository commandRepository;

    @Autowired
    private UserPermissionBusinessReadDataJPARepository queryRepository;

    @Override
    @Transactional
    public void create(List<UserPermissionBusinessDto> userRoleBusiness) {
        List<UserPermissionBusiness> saveUserRoleBusinesses = new ArrayList<>();
        for (UserPermissionBusinessDto userRoleBusines : userRoleBusiness) {
            //  RulesChecker.checkRule(new UserRoleBusinessMustBeUniqueRule(this, userRoleBusines));

            saveUserRoleBusinesses.add(new UserPermissionBusiness(userRoleBusines));
        }

        this.commandRepository.saveAll(saveUserRoleBusinesses);
    }

    @Override
    public void update(List<UserPermissionBusinessDto> userRoleBusiness) {
        List<UserPermissionBusiness> userRoleBusinesses = userRoleBusiness.stream()
                .map(UserPermissionBusiness::new)
                .collect(Collectors.toList());

        this.commandRepository.saveAll(userRoleBusinesses);
    }

    @Override
    public void delete(UserPermissionBusinessDto delete) {
        this.commandRepository.delete(new UserPermissionBusiness(delete));
    }

    @Override
    public void delete(List<UserPermissionBusinessDto> userRoleBusiness) {
        List<UserPermissionBusiness> deletes = new ArrayList<>();
        for (UserPermissionBusinessDto dto : userRoleBusiness) {
            dto.setDeleted(true);
            deletes.add(new UserPermissionBusiness(dto));
        }
        this.commandRepository.saveAll(deletes);
    }

    @Override
    public UserPermissionBusinessDto findById(UUID id) {
        Optional<UserPermissionBusiness> object = this.queryRepository.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.USER_PERMISSION_BUSINESS_NOT_FOUND, new ErrorField("id", "UserPermissionBusiness not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<UserPermissionBusiness> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<UserPermissionBusiness> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    @Override
    public List<UserPermissionBusinessDto> findByUserAndBusiness(UUID userSystemId, UUID businessI) {
        List<UserPermissionBusiness> data = this.queryRepository.findByUserAndBusiness(userSystemId, businessI);
        List<UserPermissionBusinessDto> permissionBusinessDtos = new ArrayList<>();
        for (UserPermissionBusiness o : data) {
            permissionBusinessDtos.add(o.toAggregate());
        }
        return permissionBusinessDtos;
    }

    private PaginatedResponse getPaginatedResponse(Page<UserPermissionBusiness> data) {
        List<UserRoleBusinessResponse> patients = new ArrayList<>();
        for (UserPermissionBusiness o : data.getContent()) {
            patients.add(new UserRoleBusinessResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<PermissionDto> getPermissionsForUserAndBusiness(UUID userId, UUID businessId) {
        Set<Permission> permissions = this.queryRepository.findPermissionsByUserIdAndBusinessId(userId, businessId);
        List<PermissionDto> permissionDtos = new ArrayList<>();
        for (Permission permission : permissions) {
            permissionDtos.add(permission.toAggregate());
        }
        return permissionDtos;
    }

    @Override
    public Long countByUserAndBusiness(UUID userId, UUID businessId) {
        return this.queryRepository.countByUserAndBusiness(userId, businessId);
    }

    @Override
    public Long countByUserAndBusinessNotDeleted(UUID userId, UUID businessId) {
        return this.queryRepository.countByUserAndBusinessAndNotDeleted(userId, businessId);
    }

}
