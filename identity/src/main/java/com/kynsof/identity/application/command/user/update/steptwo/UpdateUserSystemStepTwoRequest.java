package com.kynsof.identity.application.command.user.update.steptwo;

import com.kynsof.share.core.domain.EUserType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateUserSystemStepTwoRequest {
    private UUID id;
    private String image;
    private EUserType userType;
}
