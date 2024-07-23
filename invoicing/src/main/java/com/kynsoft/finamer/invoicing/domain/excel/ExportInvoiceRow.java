package com.kynsoft.finamer.invoicing.domain.excel;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsof.share.core.application.excel.annotation.SheetIndex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;

@Getter
@AllArgsConstructor
public class ExportInvoiceRow {

    @SheetIndex
    private int sheetIndex;

    @Cell(position = 0,cellType = CustomCellType.STRING)
    private String invoiceId;


    @Cell(position = 1,cellType = CustomCellType.STRING)
    private String invoiceNumber;

    @Cell(position = 2,cellType = CustomCellType.STRING)
    private String invoiceDate;

    @Cell(position = 3,cellType = CustomCellType.STRING)
    private String isManual;

    @Cell(position = 4,cellType = CustomCellType.STRING)
    private String invoiceAmount;


    @Cell(position = 5,cellType = CustomCellType.STRING)
    private String hotel;

    @Cell(position = 6,cellType = CustomCellType.STRING)
    private String agency;

    @Cell(position = 7,cellType = CustomCellType.STRING)
    private String invoiceType;

    @Cell(position = 8,cellType = CustomCellType.STRING)
    private String status;


    private CellStyle cellStyle;
}
