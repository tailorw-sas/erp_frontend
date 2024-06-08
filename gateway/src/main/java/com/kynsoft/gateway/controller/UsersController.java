//package com.kynsoft.gateway.controller;
//
//
//import com.kynsof.share.core.domain.response.ApiError;
//import com.kynsof.share.core.domain.response.ApiResponse;
//import com.kynsof.share.core.infrastructure.bus.IMediator;
//import com.kynsoft.gateway.application.command.user.changePassword.ChangePasswordCommand;
//import com.kynsoft.gateway.application.command.user.changePassword.ChangePasswordMessage;
//import com.kynsoft.gateway.application.command.user.update.UpdateUserCommand;
//import com.kynsoft.gateway.application.command.user.update.UpdateUserMessage;
//import com.kynsoft.gateway.domain.dto.user.ChangePasswordRequest;
//import com.kynsoft.gateway.domain.dto.user.ChangeStatusRequest;
//import com.kynsoft.gateway.domain.dto.user.UserRequest;
//import com.kynsoft.gateway.domain.interfaces.IUserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/users")
////@PreAuthorize("hasRole('ADMIN_CLIENT')")
//public class UsersController {
//
//    private final IUserService userService;
//    private final IMediator mediator;
//
//    @Autowired
//    public UsersController(IUserService userService, IMediator mediator) {
//        this.userService = userService;
//        this.mediator = mediator;
//    }
//
//    @GetMapping("/list")
//    public Mono<ResponseEntity<?>> findAllUsers() {
//        return Mono.justOrEmpty(ResponseEntity.ok("userService.findAllUsers()"));
//    }
////    @PostMapping("/register")
////    public ResponseEntity<ApiResponse<?>> registerSystemUser(@RequestBody UserSystemRequest userRequest) {
////        RegistrySystemUserCommand command = new RegistrySystemUserCommand(userRequest.getUserName(), userRequest.getEmail(), userRequest.getName(),
////                userRequest.getLastName(), userRequest.getPassword(), null, userRequest.getUserType());
////        RegistrySystemUserMessage registryMessage = mediator.send(command);
////        return ResponseEntity.ok(ApiResponse.success(registryMessage.getId()));
////    }
//
////    @GetMapping("/find/{username}")
////    public Mono<ResponseEntity<?>> searchUserByUsername(@PathVariable String username) {
////        return Mono.justOrEmpty(ResponseEntity.ok(userService.searchUserByUsername(username)));
////    }
//
//    @PostMapping("/change_status")
//    public Mono<ResponseEntity<?>> changeStatus(@RequestBody ChangeStatusRequest request) {
//        userService.changeStatus(request.getUserId(), request.getStatus());
//        return Mono.justOrEmpty(ResponseEntity.ok("User updated successfully"));
//    }
//
//    @PatchMapping("/update/{id}")
//    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {
//        try {
//            UpdateUserCommand command = new UpdateUserCommand(id, userRequest.getUserName(), userRequest.getEmail(),
//                    userRequest.getName(), userRequest.getLastName(), userRequest.getPassword(), null);
//            UpdateUserMessage response = mediator.send(command);
//            return ResponseEntity.ok(ApiResponse.success(response.getResult()));
//        } catch (Exception e) {
//            return ResponseEntity.ok(ApiResponse.fail(ApiError.withSingleError("error", "user/password", "User o Password incorrectos")));
//        }
//    }
//
//
//    @DeleteMapping("/delete/{id}")
//    public Mono<ResponseEntity<?>> deleteUser(@PathVariable UUID id) {
//        userService.changeStatus(id, "delete");
//        return Mono.justOrEmpty(ResponseEntity.ok("User deleted successfully"));
//    }
//
//    @PostMapping("/change-password")
//    public ResponseEntity<ApiResponse<?>> changePassword(@AuthenticationPrincipal Jwt jwt, @RequestBody ChangePasswordRequest request) {
//        try {
//            String userId = jwt.getClaim("sub");
//            ChangePasswordCommand command = new ChangePasswordCommand(userId, request.getNewPassword(), request.getOldPassword());
//            ChangePasswordMessage response = mediator.send(command);
//            return ResponseEntity.ok(ApiResponse.success(response.getResult()));
//        } catch (Exception e) {
//            return ResponseEntity.ok(ApiResponse.fail(ApiError.withSingleError("error", "user/password", "User o Password incorrectos")));
//        }
//
//    }
//}
