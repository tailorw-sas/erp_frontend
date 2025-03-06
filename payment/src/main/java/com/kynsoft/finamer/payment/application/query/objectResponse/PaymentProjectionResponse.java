package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
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
public class PaymentProjectionResponse implements IResponse {

    private UUID id;
    private Long paymentId;

    private Double paymentAmount;
    private Double paymentBalance;
    private double depositAmount;
    private double depositBalance;
    private double otherDeductions;
    private double identified;
    private double notIdentified;
    private Double notApplied;
    private Double applied;

    private UUID agencyId;
    private UUID hotelId;

    public PaymentProjectionResponse(PaymentProjection dto) {
        this.id = dto.getId();
        this.paymentId = dto.getPaymentId();
        this.paymentAmount = BankerRounding.round(dto.getPaymentAmount());
        this.paymentBalance = BankerRounding.round(dto.getPaymentBalance());
        this.depositAmount = BankerRounding.round(dto.getDepositAmount());
        this.depositBalance = BankerRounding.round(dto.getDepositBalance());
        this.otherDeductions = BankerRounding.round(dto.getOtherDeductions());
        this.identified = BankerRounding.round(dto.getIdentified());
        this.notIdentified = BankerRounding.round(dto.getNotIdentified());
        this.notApplied = BankerRounding.round(dto.getNotApplied() != null ? dto.getNotApplied() : 0.0);
        this.applied = BankerRounding.round(dto.getApplied() != null ? dto.getApplied() : 0.0);
        this.agencyId = dto.getAgencyId();
        this.hotelId = dto.getHotelId();
    }

}
