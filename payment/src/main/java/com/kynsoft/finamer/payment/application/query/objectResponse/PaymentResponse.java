package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentResponse implements IResponse {

    private UUID id;
    private OffsetDateTime createdAt;
    private Long paymentId;
    private ManagePaymentSourceResponse paymentSource;
    private String reference;
    private LocalDate transactionDate;
    private ManagePaymentStatusResponse paymentStatus;
    private ManageClientResponse client;
    private ManageAgencyResponse agency;
    private ManageHotelResponse hotel;
    private ManageBankAccountResponse bankAccount;
    private ManagePaymentAttachmentStatusResponse attachmentStatus;

    private Double paymentAmount;
    private Double paymentBalance;
    private double depositAmount;
    private double depositBalance;
    private double otherDeductions;
    private double identified;
    private double notIdentified;
    private Double notApplied;
    private Double applied;
    private String remark;
    private Boolean hasAttachment;
    private boolean hasDetailTypeDeposit = false;
    private EAttachment eAttachment;
    private boolean paymentSupport;
    private boolean createByCredit;
    private boolean existDetails;
    private ImportType importType;
//    private List<MasterPaymentAttachmentResponse> attachments = new ArrayList<>();

    public PaymentResponse(PaymentDto dto) {
        this.id = dto.getId();
        this.createdAt = dto.getCreatedAt();
        this.paymentId = dto.getPaymentId();
        this.paymentSource = dto.getPaymentSource() != null ? new ManagePaymentSourceResponse(dto.getPaymentSource()) : null;
        this.reference = dto.getReference();
        this.transactionDate = dto.getTransactionDate();
        this.paymentStatus = dto.getPaymentStatus() != null ? new ManagePaymentStatusResponse(dto.getPaymentStatus()) : null;
        this.client = dto.getClient() != null ? new ManageClientResponse(dto.getClient()) : null;
        this.agency = dto.getAgency() != null ? new ManageAgencyResponse(dto.getAgency()) : null;
        this.hotel = dto.getHotel() != null ? new ManageHotelResponse(dto.getHotel()) : null;
        this.bankAccount = dto.getBankAccount() != null ? new ManageBankAccountResponse(dto.getBankAccount()) : null;
        this.attachmentStatus = dto.getAttachmentStatus() != null ? new ManagePaymentAttachmentStatusResponse(dto.getAttachmentStatus()) : null;
        this.paymentAmount = BankerRounding.round(dto.getPaymentAmount());
        this.paymentBalance = BankerRounding.round(dto.getPaymentBalance());
        this.depositAmount = BankerRounding.round(dto.getDepositAmount());
        this.depositBalance = BankerRounding.round(dto.getDepositBalance());
        this.otherDeductions = BankerRounding.round(dto.getOtherDeductions());
        this.identified = BankerRounding.round(dto.getIdentified());
        this.notIdentified = BankerRounding.round(dto.getNotIdentified());
        this.notApplied = BankerRounding.round(dto.getNotApplied() != null ? dto.getNotApplied() : 0.0);
        this.applied = BankerRounding.round(dto.getApplied() != null ? dto.getApplied() : 0.0);
        this.remark = dto.getRemark();
        this.eAttachment = dto.getEAttachment();
        this.paymentSupport = dto.isPaymentSupport();
        this.createByCredit = dto.isCreateByCredit();
        this.importType = dto.getImportType();
        if (dto.getPaymentDetails() == null) {
            this.existDetails = false;
        } else {
            this.existDetails = !dto.getPaymentDetails().isEmpty();
        }
//        if (dto.getAttachments() != null) {
//            for (MasterPaymentAttachmentDto attachment : dto.getAttachments()) {
//                attachments.add(new MasterPaymentAttachmentResponse(attachment));
//            }
//        }

        this.hasAttachment = dto.getAttachments() != null;
        if (dto.getInvoice() != null) {
            if (dto.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT)) {
                this.hasAttachment = dto.getInvoice().getHasAttachment();
            }
        }
        if (dto.getPaymentDetails() != null) {
            for (PaymentDetailDto paymentDetail : dto.getPaymentDetails()) {
                if (paymentDetail.getTransactionType().getDeposit()) {
                    this.hasDetailTypeDeposit = true;
                    break;
                }
            }
        }
    }

}
