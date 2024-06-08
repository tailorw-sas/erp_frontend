package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.users.userMe.BusinessPermissionResponse;
import com.kynsof.identity.application.query.users.userMe.UserMeResponse;
import com.kynsof.identity.domain.interfaces.service.IUserMeService;
import com.kynsof.identity.infrastructure.identity.Business;
import com.kynsof.identity.infrastructure.identity.UserPermissionBusiness;
import com.kynsof.identity.infrastructure.identity.UserSystem;
import com.kynsof.identity.infrastructure.repository.query.UserPermissionBusinessReadDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.UserSystemReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserMeServiceImpl implements IUserMeService {


    private final UserPermissionBusinessReadDataJPARepository  userPermissionBusinessReadDataJPARepository;
    private final UserSystemReadDataJPARepository userSystemReadDataJPARepository;

    public UserMeServiceImpl(UserPermissionBusinessReadDataJPARepository userPermissionBusinessReadDataJPARepository, UserSystemReadDataJPARepository userSystemReadDataJPARepository) {
        this.userPermissionBusinessReadDataJPARepository = userPermissionBusinessReadDataJPARepository;
        this.userSystemReadDataJPARepository = userSystemReadDataJPARepository;
    }

    @Override
//    @Cacheable(cacheNames =  CacheConfig.USER_CACHE, unless = "#result == null", key = "#userId")
    public UserMeResponse getUserInfo(UUID userId) {
        Optional<UserSystem> userSystem = userSystemReadDataJPARepository.findById(userId);
        List<UserPermissionBusiness> userPermissions = this.userPermissionBusinessReadDataJPARepository.findUserPermissionBusinessByUserId(userId);

        Map<UUID, BusinessPermissionResponse> businessResponses = userPermissions.stream()
                .collect(Collectors.groupingBy(upb -> upb.getBusiness().getId()))
                .values().stream()
                .map(userPermissionBusinesses -> {
                    List<String> permissions = userPermissionBusinesses.stream()
                            .map(upb -> upb.getPermission().getCode())
                            .distinct()
                            .collect(Collectors.toList());
                    Business business = userPermissionBusinesses.get(0).getBusiness();

                    return new BusinessPermissionResponse(
                            business.getId(),
                            business.getName(),
                            permissions);
                })
                .collect(Collectors.toMap(BusinessPermissionResponse::getBusinessId, bpr -> bpr));
        return getUserMeResponse(userSystem.get(),userPermissions, businessResponses);
    }

    private static UserMeResponse getUserMeResponse(UserSystem userSystem,List<UserPermissionBusiness> userPermissions, Map<UUID, BusinessPermissionResponse> businessResponses) {
        return new UserMeResponse(
                userSystem.getId(),
                userSystem.getUserName(),
                userSystem.getEmail(),
                userSystem.getName(),
                userSystem.getLastName(),
                userSystem.getImage(),
                userSystem.getSelectedBusiness() ,
                new ArrayList<>(businessResponses.values()) // Convertimos el mapa a una lista
        );
    }
}
