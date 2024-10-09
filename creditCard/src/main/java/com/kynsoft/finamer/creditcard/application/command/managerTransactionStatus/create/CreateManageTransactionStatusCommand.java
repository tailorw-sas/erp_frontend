package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateManageTransactionStatusCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Set<UUID> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;

    private boolean sentStatus;
    private boolean refundStatus;
    private boolean receivedStatus;

    public CreateManageTransactionStatusCommand(
            String code, String description, String name,
            Set<UUID> navigate, Boolean enablePayment,
            Boolean visible, Status status, boolean sentStatus,
            boolean refundStatus, boolean receivedStatus) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.navigate = navigate;
        this.enablePayment = enablePayment;
        this.visible = visible;
        this.status = status;
        this.sentStatus = sentStatus;
        this.refundStatus = refundStatus;
        this.receivedStatus = receivedStatus;
    }

    public static CreateManageTransactionStatusCommand fromRequest(CreateManageTransactionStatusRequest request) {
        return new CreateManageTransactionStatusCommand(
                request.getCode(),
                request.getDescription(),
                request.getName(),
                request.getNavigate(),
                request.getEnablePayment(),
                request.getVisible(),
                request.getStatus(),
                request.isSentStatus(),
                request.isRefundStatus(),
                request.isReceivedStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageTransactionStatusMessage(id);
    }
}
