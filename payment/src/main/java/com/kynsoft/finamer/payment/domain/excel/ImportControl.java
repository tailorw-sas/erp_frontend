package com.kynsoft.finamer.payment.domain.excel;

import com.kynsof.share.core.application.excel.validator.IImportControl;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportControl implements IImportControl {

    private Boolean shouldStopProcess;

    public ImportControl(Boolean shouldStopProcess){
        this.shouldStopProcess = shouldStopProcess;
    }
}
