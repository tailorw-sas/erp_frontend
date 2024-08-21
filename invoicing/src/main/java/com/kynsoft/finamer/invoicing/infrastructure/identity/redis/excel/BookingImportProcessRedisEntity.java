package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel;

import com.kynsoft.finamer.invoicing.domain.dto.BookingImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "bookingimportprocess",timeToLive = 3600)
public class BookingImportProcessRedisEntity implements Serializable {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private EProcessStatus status;
    private boolean hasError;
    private String exceptionMessage;

    public BookingImportProcessRedisEntity(String id, String importProcessId, EProcessStatus status) {
        this.id = id;
        this.importProcessId = importProcessId;
        this.status = status;
    }


    public BookingImportProcessDto toAgreggate(){
        return new BookingImportProcessDto(this.id,this.importProcessId,this.status,this.hasError,this.exceptionMessage);
    }
}
