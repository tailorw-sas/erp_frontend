package com.kynsoft.finamer.settings.application.command.manageCollectionStatus.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class UpdateManageCollectionStatusRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledPayment;
    private Boolean isVisible;
    private List<UUID> navigate;
}
