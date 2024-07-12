package com.kynsof.share.core.application.excel;

import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Date;

public class ExcelUtils {

    public static Object getValueFromCell(CellInfo cellInfo, Cell cell){
       if (cellInfo.getCellType().equals(CustomCellType.BOOLEAN)){
           return cell.getBooleanCellValue();
       } else if (CustomCellType.STRING.equals(cellInfo.getCellType())) {
           return cell.getStringCellValue();
       } else if (CustomCellType.DATE.equals(cellInfo.getCellType())) {
           return cell.getDateCellValue();
       }else  if (CustomCellType.DATETIME.equals(cellInfo.getCellType())){
           return cell.getLocalDateTimeCellValue();
       }else {
           return cell.getNumericCellValue();
       }
    }
}
