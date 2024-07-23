package com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@RedisHash(value = "importprocess",timeToLive = 7200)
public class ImportProcess {
    @Id
    private String id;
    @Indexed
    private String importProcessId;
    private ProcessStatus status;

}
