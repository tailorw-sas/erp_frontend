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
    private String invoiceId;//Id

    @Cell(position = 1,cellType = CustomCellType.STRING)
    private String invoiceType;//Type
    
    @Cell(position = 2,cellType = CustomCellType.STRING)
    private String hotel;//Hotel

    @Cell(position = 3,cellType = CustomCellType.STRING)
    private String agencyCd;//Agency

    @Cell(position = 4,cellType = CustomCellType.STRING)
    private String agency;//Agency

    @Cell(position = 5,cellType = CustomCellType.STRING)
    private String invoiceNumber;//Inv. No

    @Cell(position = 6,cellType = CustomCellType.STRING)
    private String invoiceDate;//Gen. Date

    @Cell(position = 7,cellType = CustomCellType.STRING)
    private String status;//Status

    @Cell(position = 8,cellType = CustomCellType.STRING)
    private String isManual;//Manual

    @Cell(position = 9,cellType = CustomCellType.STRING)
    private String invoiceAmount;//Amount

    @Cell(position = 10,cellType = CustomCellType.STRING)
    private String dueAmount;//Due Amount

    @Cell(position = 11,cellType = CustomCellType.STRING)
    private String autoRec;//Auto Rec

    private CellStyle cellStyle;
}
