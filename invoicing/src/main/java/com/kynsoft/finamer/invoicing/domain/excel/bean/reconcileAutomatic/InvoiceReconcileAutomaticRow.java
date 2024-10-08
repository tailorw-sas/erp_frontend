package com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceReconcileAutomaticRow {

    private String importProcessId;

    @Cell(position = -1, headerName = "")
    private int rowNumber;

    @Cell(position = 0)
    private String columnA;
    @Cell(position = 23)
    private String columnW;
    @Cell(position = 40,cellType = CustomCellType.NUMERIC)
    private String columnAN;
    @Cell(position = 4)
    private String columnE;
    @Cell(position = 11)
    private String columnL;
    @Cell(position = 13)
    private String columnN;

}
