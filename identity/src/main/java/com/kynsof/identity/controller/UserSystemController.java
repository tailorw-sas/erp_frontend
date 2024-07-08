package com.kynsof.identity.controller;

import com.kynsof.identity.application.command.auth.sendPasswordRecoveryOtp.SendPasswordRecoveryOtpCommand;
import com.kynsof.identity.application.command.auth.sendPasswordRecoveryOtp.SendPasswordRecoveryOtpMessage;
import com.kynsof.identity.application.command.user.changePassword.ChangePasswordCommand;
import com.kynsof.identity.application.command.user.changePassword.ChangePasswordMessage;
import com.kynsof.identity.application.command.user.changePassword.ChangePasswordRequest;
import com.kynsof.identity.application.command.user.changeSelectedBusiness.ChangeSelectedBusinessCommand;
import com.kynsof.identity.application.command.user.changeSelectedBusiness.ChangeSelectedBusinessMessage;
import com.kynsof.identity.application.command.user.changeSelectedBusiness.ChangeSelectedBusinessRequest;
import com.kynsof.identity.application.command.user.create.CreateUserSystemCommand;
import com.kynsof.identity.application.command.user.create.CreateUserSystemMessage;
import com.kynsof.identity.application.command.user.create.CreateUserSystemRequest;
import com.kynsof.identity.application.command.user.delete.DeleteUserSystemsCommand;
import com.kynsof.identity.application.command.user.delete.DeleteUserSystemsMessage;
import com.kynsof.identity.application.command.user.deleteAll.DeleteAllUserSystemRequest;
import com.kynsof.identity.application.command.user.deleteAll.DeleteAllUserSystemsCommand;
import com.kynsof.identity.application.command.user.deleteAll.DeleteAllUserSystemsMessage;
import com.kynsof.identity.application.command.user.sendPassword.SendPasswordCommand;
import com.kynsof.identity.application.command.user.sendPassword.SendPasswordMessage;
import com.kynsof.identity.application.command.user.sendPassword.SendPasswordRequest;
import com.kynsof.identity.application.command.user.update.UpdateUserSystemCommand;
import com.kynsof.identity.application.command.user.update.UpdateUserSystemMessage;
import com.kynsof.identity.application.command.user.update.UpdateUserSystemRequest;
import com.kynsof.identity.application.command.user.update.steptwo.UpdateUserSystemStepTwoCommand;
import com.kynsof.identity.application.command.user.update.steptwo.UpdateUserSystemStepTwoMessage;
import com.kynsof.identity.application.command.user.update.steptwo.UpdateUserSystemStepTwoRequest;
import com.kynsof.identity.application.query.users.getById.FindByIdUserSystemsQuery;
import com.kynsof.identity.application.query.users.getById.UserSystemsByIdResponse;
import com.kynsof.identity.application.query.users.getSearch.GetSearchUserSystemsQuery;
import com.kynsof.identity.application.query.users.userMe.UserMeQuery;
import com.kynsof.identity.application.query.users.userMe.UserMeResponse;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.ApiError;
import com.kynsof.share.core.domain.response.ApiResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserSystemController {

    private final IMediator mediator;

    @Autowired
    public UserSystemController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> createUserSystem(@RequestBody CreateUserSystemRequest request) {
        CreateUserSystemCommand command = CreateUserSystemCommand.fromRequest(request);
        CreateUserSystemMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }


    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindByIdUserSystemsQuery query = new FindByIdUserSystemsQuery(id);
        UserSystemsByIdResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateUserSystemRequest request) {

        UpdateUserSystemCommand command = UpdateUserSystemCommand.fromRequest(id, request);
        UpdateUserSystemMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/step/two")
    public ResponseEntity<?> updateStepTwo(@RequestBody UpdateUserSystemStepTwoRequest request) {

        UpdateUserSystemStepTwoCommand command = UpdateUserSystemStepTwoCommand.fromRequest(request);
        UpdateUserSystemStepTwoMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServices(@PathVariable("id") UUID id) {

        DeleteUserSystemsCommand command = new DeleteUserSystemsCommand(id);
        DeleteUserSystemsMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchUserSystemsQuery query = new GetSearchUserSystemsQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
		try {
            String userId = jwt.getClaim("sub");
            UserMeQuery query = new UserMeQuery(UUID.fromString(userId));
            UserMeResponse response = mediator.send(query);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(ApiError.withSingleError("error", "token", "Error al procesar el token")));
        }
    }

    @PatchMapping(path = "/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordRequest request) {
        ChangePasswordCommand command = ChangePasswordCommand.fromRequest(id, request);
        ChangePasswordMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/change-password-otp")
    public ResponseEntity<?> changePasswordOtp(@RequestParam String email,  @AuthenticationPrincipal Jwt jwt) {
        SendPasswordRecoveryOtpCommand command = new SendPasswordRecoveryOtpCommand(email);
        SendPasswordRecoveryOtpMessage sendPasswordRecoveryOtpMessage = mediator.send(command);
        return ResponseEntity.ok(ApiResponse.success(sendPasswordRecoveryOtpMessage.getResult()));
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> delete(@RequestBody DeleteAllUserSystemRequest request) {

        DeleteAllUserSystemsCommand command = new DeleteAllUserSystemsCommand(request.getUsers());
        DeleteAllUserSystemsMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/send-password")
    public ResponseEntity<?> generatePassword(@RequestBody SendPasswordRequest request) {
        SendPasswordCommand command =  SendPasswordCommand.fromRequest(request);
        SendPasswordMessage response = mediator.send(command);
        return ResponseEntity.ok(ApiResponse.success(response.getResult()));
    }

    @PatchMapping(path = "/{id}/business")
    public ResponseEntity<?> changeSelectedBusiness(@PathVariable UUID id,@RequestBody ChangeSelectedBusinessRequest request) {
        ChangeSelectedBusinessCommand command =  ChangeSelectedBusinessCommand.fromRequest(id, request);
        ChangeSelectedBusinessMessage result = mediator.send(command);
        return ResponseEntity.ok(ApiResponse.success(result.getResult()));
    }
}