package com.kynsoft.finamer.payment.application.command.paymentDetail.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentDetailCommand implements ICommand {
    private UUID id;
    private String remark;

    public UpdatePaymentDetailCommand(UUID id, String remark) {
        this.id = id;
        this.remark = remark;
    }

    public static UpdatePaymentDetailCommand fromRequest(UpdatePaymentDetailRequest request, UUID id) {
        return new UpdatePaymentDetailCommand(
                id,
                request.getRemark()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdatePaymentDetailMessage(id);
    }
}
