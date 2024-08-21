package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookingImportProcessDto {

    private String id;
    private String importProcessId;
    private EProcessStatus status;
    private boolean hasError;
    private String exceptionMessage;

    public BookingImportProcessDto(String id, String importProcessId, EProcessStatus status) {
        this.id = id;
        this.importProcessId = importProcessId;
        this.status = status;
    }

    public BookingImportProcessRedisEntity toAggregate() {
        return new BookingImportProcessRedisEntity(this.id,this.importProcessId,this.status,this.hasError,this.exceptionMessage);
    }
}
