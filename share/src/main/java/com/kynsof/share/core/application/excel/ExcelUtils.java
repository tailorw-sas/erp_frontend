package com.kynsof.share.core.application.excel;

import com.kynsof.share.core.application.excel.procesor.SheetIndexAnnotationProcessor;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.exception.ReadExcelException;
import com.kynsof.share.core.domain.response.ErrorField;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class ExcelUtils {

    public static Object getValueFromCell(CellInfo cellInfo, Cell cell, DataFormat dataFormatter,DataFormatter formatter) {

        if (Objects.isNull(cell)) {
            return null;
        }
        if (cellInfo.getCellType().equals(CustomCellType.BOOLEAN)) {
            return cell.getBooleanCellValue();
        } else if (CustomCellType.STRING.equals(cellInfo.getCellType())) {
            return cell.getStringCellValue();
        } else if (CustomCellType.DATE.equals(cellInfo.getCellType())) {
            return cell.getDateCellValue();
        } else if (CustomCellType.DATETIME.equals(cellInfo.getCellType())) {
            return cell.getLocalDateTimeCellValue();
        } else if (CustomCellType.DATAFORMAT.equals(cellInfo.getCellType())) {
            return formatter.formatCellValue(cell);
        } else if (CustomCellType.ALFANUMERIC.equals(cellInfo.getCellType())) {
            return switch (cell.getCellType()) {
                case _NONE -> null;
                case NUMERIC -> {
                    short format = dataFormatter.getFormat("0");
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setDataFormat(format);
                    cell.setCellStyle(cellStyle);
                    yield formatter.formatCellValue(cell);
                }
                case STRING -> {
                    short format = dataFormatter.getFormat("@");
                    CellStyle cellStyle = cell.getCellStyle();
                    cellStyle.setDataFormat(format);
                    cell.setCellStyle(cellStyle);
                    yield formatter.formatCellValue(cell);
                }
                case FORMULA -> null;
                case BLANK -> null;
                case BOOLEAN -> null;
                case ERROR -> null;
            };

        } else {
            return cell.getNumericCellValue();
        }
    }

    public static void setCellValue(CellInfo cellInfo, Cell cell, Object value, Class<?> type) {
        if (cellInfo.getCellType().equals(CustomCellType.BOOLEAN)) {
            cell.setCellValue((boolean) value);
        } else if (CustomCellType.STRING.equals(cellInfo.getCellType())) {
            cell.setCellValue((String) value);
        } else if (CustomCellType.DATE.equals(cellInfo.getCellType())) {
            cell.setCellValue((Date) value);
        } else if (CustomCellType.DATETIME.equals(cellInfo.getCellType())) {
            cell.setCellValue((LocalDateTime) value);
        } else if (CustomCellType.FORMULA.equals(cellInfo.getCellType())) {
            cell.setCellFormula((String) value);
        } else if (type.isAssignableFrom(Double.class)) {
            cell.setCellValue((Double) value);
        } else if (type.isAssignableFrom(double.class)) {
            cell.setCellValue((double) value);
        } else if (type.isAssignableFrom(int.class)) {
            cell.setCellValue((int) value);
        }
    }

    public static boolean isEndOfContent(Integer rowCursor, Sheet sheetToRead) {
        return rowCursor > sheetToRead.getLastRowNum();
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCellEmpty(Cell cell) {
        return Objects.isNull(cell) || cell.getCellType() == CellType.BLANK;
    }

    public static void readCell(Cell cell, BeanField beanField, CellInfo cellInfo, Object bean, DataFormat dataFormatter,DataFormatter formatter) {
        try {
            beanField.setFieldValue(ExcelUtils.getValueFromCell(cellInfo, cell, dataFormatter,formatter ), bean);

        } catch (Exception e) {
            throw new ExcelException("Invalid excel content");
        }
    }

    public static boolean isSheetEmpty(Sheet currentSheet) {
        return currentSheet.getLastRowNum() <= 0 && currentSheet.getRow(0) == null;
    }

    public static SheetIndex getSheetIndexInfo(SheetIndexAnnotationProcessor sheetIndexAnnotationProcessor, Object bean) {
        BeanField annotatedField = sheetIndexAnnotationProcessor.getBeanField();
        Assert.notNull(annotatedField, "Must be a field annotated as SheetIndex  ");
        return new SheetIndex((Integer) annotatedField.getFieldValue(bean));
    }
}
