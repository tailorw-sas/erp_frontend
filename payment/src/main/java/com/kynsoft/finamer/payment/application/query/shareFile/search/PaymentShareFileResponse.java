package com.kynsoft.finamer.payment.application.query.shareFile.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentShareFileResponse implements IResponse {

    private final UUID id;
    private final PaymentResponse payment;
    private final String fileName;
    private final String fileUrl;
    private final int shareFileYear;
    private final int shareFileMonth;

    public PaymentShareFileResponse(PaymentShareFileDto shareFile) {
        this.id = shareFile.getId();
        this.payment = new PaymentResponse(shareFile.getPayment());
        this.fileName = shareFile.getFileName();
        this.fileUrl = shareFile.getFileUrl();
        this.shareFileYear = shareFile.getShareFileYear();
        this.shareFileMonth = shareFile.getShareFileMonth();
    }

}
