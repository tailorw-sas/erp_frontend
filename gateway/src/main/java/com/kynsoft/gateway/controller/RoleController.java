//package com.kynsoft.gateway.controller;
//
//import com.kynsof.share.core.domain.response.ApiResponse;
//import com.kynsof.share.core.infrastructure.bus.IMediator;
//import com.kynsoft.gateway.application.command.role.create.CreateRoleCommand;
//import com.kynsoft.gateway.application.command.role.create.CreateRoleMessage;
//import com.kynsoft.gateway.domain.dto.role.RoleRequest;
//import com.kynsoft.gateway.domain.interfaces.IRoleService;
//import org.keycloak.representations.idm.RoleRepresentation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.net.URISyntaxException;
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/role")
////@PreAuthorize("hasRole('ADMIN_CLIENT')")
//public class RoleController {
//    private final IRoleService roleService;
//
//    private final IMediator mediator;
//
//    @Autowired
//    public RoleController(IRoleService roleService, IMediator mediator) {
//        this.roleService = roleService;
//        this.mediator = mediator;
//    }
//
//    @PreAuthorize("permitAll()")
//    @GetMapping("")
//    public Mono<ResponseEntity<List<RoleRepresentation>>> getALL() {
//        List<RoleRepresentation> response = roleService.findAllRoles();
//        return Mono.just(ResponseEntity.ok(response));
//    }
//
//    @PostMapping("")
//    public ResponseEntity<ApiResponse<UUID>> createRole(@RequestBody RoleRequest registerDTO) throws URISyntaxException {
//        CreateRoleCommand command = new CreateRoleCommand(registerDTO.getName(), registerDTO.getDescription());
//        CreateRoleMessage roleMessage = this.mediator.send(command);
//        return ResponseEntity.ok(ApiResponse.success(roleMessage.getId()));
////        String response = roleService.createRole(registerDTO);
////        return Mono.justOrEmpty(ResponseEntity.created(new URI("/role")).body(response));
//    }
//
//}
