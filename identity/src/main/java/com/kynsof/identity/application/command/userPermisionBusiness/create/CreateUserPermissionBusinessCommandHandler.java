package com.kynsof.identity.application.command.userPermisionBusiness.create;

import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.*;
import com.kynsof.identity.infrastructure.services.kafka.producer.userBusiness.ProducerCreateUserBusinessRelationEventService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateUserPermissionBusinessCommandHandler implements ICommandHandler<CreateUserPermissionBusinessCommand> {

    private final IUserPermissionBusinessService service;

    private final IPermissionService permissionService;

    private final IBusinessService businessService;

    private final IUserSystemService userSystemService;
    private final IRedisService redisService;

    private final ProducerCreateUserBusinessRelationEventService createUserBusinessEventService;

    public CreateUserPermissionBusinessCommandHandler(IUserPermissionBusinessService service,
                                                      IPermissionService permissionService,
                                                      IBusinessService businessService,
                                                      IUserSystemService userSystemService, IRedisService redisService,
                                                      ProducerCreateUserBusinessRelationEventService createUserBusinessEventService) {
        this.service = service;
        this.permissionService = permissionService;
        this.businessService = businessService;
        this.userSystemService = userSystemService;
        this.redisService = redisService;
        this.createUserBusinessEventService = createUserBusinessEventService;
    }

    @Override
    public void handle(CreateUserPermissionBusinessCommand command) {
        List<UserPermissionBusinessDto> userRoleBusinessDtos = new ArrayList<>();

      UserPermissionBusinessRequest userRoleBusinessRequest = command.getPayload();
            UserSystemDto userSystemDto = this.userSystemService.findById(userRoleBusinessRequest.getUserId());
            BusinessDto businessDto = this.businessService.findById(userRoleBusinessRequest.getBusinessId());

            for (UUID role : userRoleBusinessRequest.getPermissionIds()) {
                PermissionDto roleDto = this.permissionService.findById(role);
                userRoleBusinessDtos.add(new UserPermissionBusinessDto(UUID.randomUUID(), userSystemDto, roleDto, businessDto));
            }

        redisService.deleteKey(command.getPayload().getUserId().toString());
        this.service.create(userRoleBusinessDtos);
        this.createUserBusinessEventService.create(userRoleBusinessDtos.get(0));
    }
}