package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IUserPermissionBusinessService {
    void create(List<UserPermissionBusinessDto> userRoleBusiness);
    void update(List<UserPermissionBusinessDto> userRoleBusiness);
    void delete(UserPermissionBusinessDto delete); 
    void delete(List<UserPermissionBusinessDto> userRoleBusiness);
    UserPermissionBusinessDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<UserPermissionBusinessDto> findByUserAndBusiness(UUID userSystemId, UUID businessId);

    List<PermissionDto> getPermissionsForUserAndBusiness(UUID userId, UUID businessId);

    Long countByUserAndBusiness(UUID userId, UUID businessId);

    Long countByUserAndBusinessNotDeleted(UUID userId, UUID businessId);
}
