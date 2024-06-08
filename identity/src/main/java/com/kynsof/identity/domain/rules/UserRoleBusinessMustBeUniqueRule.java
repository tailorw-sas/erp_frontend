package com.kynsof.identity.domain.rules;

import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class UserRoleBusinessMustBeUniqueRule extends BusinessRule {

    private final IUserPermissionBusinessService service;

    private final UserPermissionBusinessDto userRoleBusinessDto;

    public UserRoleBusinessMustBeUniqueRule(IUserPermissionBusinessService service, UserPermissionBusinessDto userRoleBusinessDto) {
        super(
                DomainErrorMessage.RELATIONSHIP_MUST_BE_UNIQUE, 
                new ErrorField("UserPermissionBusiness",
                        "The role: " + 
                                userRoleBusinessDto.getPermission().getCode() + " for user: " + userRoleBusinessDto.getUser().getIdentification() +
                                " is already related to the business " + userRoleBusinessDto.getBusiness().getName())
        );
        this.service = service;
        this.userRoleBusinessDto = userRoleBusinessDto;
    }

    @Override
    public boolean isBroken() {
        return true;
    }

}
