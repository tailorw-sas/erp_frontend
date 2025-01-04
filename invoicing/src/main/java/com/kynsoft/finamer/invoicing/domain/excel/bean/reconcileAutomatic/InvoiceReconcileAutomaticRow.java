package com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvoiceReconcileAutomaticRow {

    private String importProcessId;

    @Cell(position = -1, headerName = "")
    private int rowNumber;

    @Cell(position = 0,cellType = CustomCellType.DATAFORMAT)
    private String columnA;
    @Cell(position = 22,cellType = CustomCellType.DATAFORMAT)
    private String columnW;
    @Cell(position = 39,cellType = CustomCellType.DATAFORMAT)
    private String columnAN;
    @Cell(position = 4,cellType = CustomCellType.DATAFORMAT)
    private String columnE;
    @Cell(position = 11,cellType = CustomCellType.DATAFORMAT)
    private String columnL;
    @Cell(position = 13,cellType = CustomCellType.DATAFORMAT)
    private String columnN;

    private String invoiceIds;
}
