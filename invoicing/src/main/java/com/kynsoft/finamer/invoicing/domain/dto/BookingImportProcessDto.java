package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@Builder
public class BookingImportProcessDto {

    private String id;
    private String importProcessId;
    private EProcessStatus status;
    private int total;
    private boolean hasError;
    private String exceptionMessage;


    public BookingImportProcessRedisEntity toAggregate() {
        return new BookingImportProcessRedisEntity(this.id,this.importProcessId,this.status,this.hasError,this.exceptionMessage,total);
    }
}
