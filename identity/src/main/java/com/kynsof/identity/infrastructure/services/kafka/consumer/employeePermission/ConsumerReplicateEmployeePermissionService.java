package com.kynsof.identity.infrastructure.services.kafka.consumer.employeePermission;

import com.kynsof.identity.application.command.userPermisionBusiness.create.CreateUserPermissionBusinessCommand;
import com.kynsof.identity.application.command.userPermisionBusiness.create.UserPermissionBusinessRequest;
import com.kynsof.identity.application.query.users.getById.FindByIdUserSystemsQuery;
import com.kynsof.identity.application.query.users.getById.UserSystemsByIdResponse;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.share.core.domain.kafka.entity.ReplicateEmployeePermissionKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import java.util.List;
import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ConsumerReplicateEmployeePermissionService {

    private final IMediator mediator;
    private final IBusinessService businessService;
    private final IPermissionService permissionService;

    public ConsumerReplicateEmployeePermissionService(IMediator mediator,
            IBusinessService businessService,
            IPermissionService permissionService) {
        this.mediator = mediator;
        this.businessService = businessService;
        this.permissionService = permissionService;
    }

    @KafkaListener(topics = "finamer-replicate-employee-permission", groupId = "identity-entity-replica")
    public void listen(ReplicateEmployeePermissionKafka objKafka) {
        try {
            UUID businessId = this.businessService.findAllBusiness().get(0).getId();
            UserSystemsByIdResponse employee = this.mediator.send(new FindByIdUserSystemsQuery(objKafka.getEmployee()));
            List<PermissionDto> permissionsDtos = this.permissionService.findByIds(objKafka.getPermissionIds());
            List<UUID> permissions = permissionsDtos.stream()
                    .map(PermissionDto::getId)
                    .collect(Collectors.toList());

            UserPermissionBusinessRequest request = new UserPermissionBusinessRequest();
            request.setBusinessId(businessId);
            request.setPermissionIds(permissions);
            request.setUserId(employee.getId());
            CreateUserPermissionBusinessCommand command = CreateUserPermissionBusinessCommand.fromRequest(request);

            this.mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateEmployeePermissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
