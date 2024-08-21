package com.kynsoft.finamer.payment.domain.excel;

import com.kynsoft.finamer.payment.domain.dto.PaymentImportStatusDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
@AllArgsConstructor
@RedisHash(value = "processstatus",timeToLive = 3600)
public class PaymentImportProcessStatusEntity {
    @Id
    String id;
    private String status;
    @Indexed
    private String importProcessId;

    private boolean hasError;
    private String exceptionMessage;

    public PaymentImportStatusDto toAggregate(){
        return new PaymentImportStatusDto(this.id,this.status,this.importProcessId,this.hasError,this.exceptionMessage);
    }
}
