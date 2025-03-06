package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.PaymentSearchProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

//@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
public class PaymentSearchResponse implements IResponse {

    private UUID id;
    private OffsetDateTime createdAt;
    private Long paymentId;
    private ManagePaymentSourceSearchResponse paymentSource;
    private String reference;
    private LocalDate transactionDate;
    private ManagePaymentStatusSearchResponse paymentStatus;
    private ManageClientSearchResponse client;
    private ManageAgencySearchResponse agency;
    private ManageHotelResponse hotel;
    private ManageBankAccountSearchResponse bankAccount;
    private ManagePaymentAttachmentStatusSearchResponse attachmentStatus;

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
    private boolean applyPayment;
    private boolean paymentSupport;
    private boolean createByCredit;
    private boolean existDetails;
    private ImportType importType;

    public PaymentSearchResponse(PaymentSearchProjection dto) {
        this.id = dto.getId();
        //   this.createdAt = dto.getTransactionDate();
        this.paymentId = dto.getPaymentId();
        this.reference = dto.getReference();
        this.transactionDate = dto.getTransactionDate();
        this.paymentSource = dto.getPaymentSource() != null ? new ManagePaymentSourceSearchResponse(dto.getPaymentSource()) : null;
        this.paymentStatus = dto.getPaymentStatus() != null ? new ManagePaymentStatusSearchResponse(dto.getPaymentStatus()) : null;
        this.client = dto.getClient() != null ? new ManageClientSearchResponse(dto.getClient()) : null;
        this.agency = dto.getAgency() != null ? new ManageAgencySearchResponse(dto.getAgency()) : null;
        this.hotel = dto.getHotel() != null ? new ManageHotelResponse(dto.getHotel()) : null;
        this.bankAccount = dto.getBankAccount() != null ? new ManageBankAccountSearchResponse(dto.getBankAccount()) : null;
        this.attachmentStatus = dto.getPaymentAttachmentStatus() != null ? new ManagePaymentAttachmentStatusSearchResponse(dto.getPaymentAttachmentStatus()) : null;
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
        this.applyPayment = dto.getApplyPayment();
        this.paymentSupport = dto.getPaymentSupport();
        this.createByCredit = dto.getCreateByCredit();
        this.hasDetailTypeDeposit = dto.isHasDetailTypeDeposit();
        this.hasAttachment = dto.getHasAttachment();
        this.importType = dto.getImportType();
    }
}
