package com.kynsoft.notification.application.command.file.confirm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ConfirmFileRequest {

    private List<UUID> ids;

}
