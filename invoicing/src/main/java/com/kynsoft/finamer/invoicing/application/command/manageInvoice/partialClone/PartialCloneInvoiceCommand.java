package com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartialCloneInvoiceCommand implements ICommand {

        private UUID invoice;
        private List<PartialCloneInvoiceAdjustmentRelation> roomRateAdjustments;
        private List<CreateAttachmentCommand> attachmentCommands;
        private List<UUID> bookings;
        private List<UUID> roomRates;
        private List<UUID> attachments;
        private String employee;
        private UUID cloned;

        public PartialCloneInvoiceCommand(UUID invoice, List<PartialCloneInvoiceAdjustmentRelation> roomRateAdjustments,
                        List<CreateAttachmentCommand> attachmentCommands, String employee) {
                this.invoice = invoice;
                this.roomRateAdjustments = roomRateAdjustments;
                this.attachmentCommands = attachmentCommands;
                this.employee = employee;
        }

        public static PartialCloneInvoiceCommand fromRequest(PartialCloneInvoiceRequest request) {
                return new PartialCloneInvoiceCommand(request.getInvoice(), request.getRoomRateAdjustments(),
                                request.getAttachments().stream().map(e -> CreateAttachmentCommand.fromRequest(e))
                                                .collect(Collectors.toList()), request.getEmployee());
        }

        @Override
        public ICommandMessage getMessage() {
                return new PartialCloneInvoiceMessage(invoice, cloned);
        }
}
