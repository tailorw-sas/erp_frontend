package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportProcessStatusEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentImportStatusDto {
    private String id;
    private String status;
    private String importProcessId;
    private boolean hasError;
    private String exceptionMessage;

    public PaymentImportStatusDto(String status, String importProcessId) {
        this.status = status;
        this.importProcessId = importProcessId;
    }

    public PaymentImportProcessStatusEntity toAggregate() {
       return new PaymentImportProcessStatusEntity(this.id,
               this.status,
               this.importProcessId,
               this.hasError,
               this.exceptionMessage);

    }
}
