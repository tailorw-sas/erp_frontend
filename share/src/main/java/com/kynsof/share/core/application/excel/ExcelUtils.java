package com.kynsof.share.core.application.excel;

import com.kynsof.share.core.application.excel.procesor.SheetIndexAnnotationProcessor;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.utils.BankerRounding;
import io.jsonwebtoken.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.stream.IntStream;

public class ExcelUtils {

    public static Object getValueFromCell(CellInfo cellInfo, Cell cell, DataFormat dataFormatter,DataFormatter formatter) {
        if (Objects.isNull(cell)) {
            return null;
        }
        switch (cellInfo.getCellType()) {
            case BOOLEAN:
                return cell.getBooleanCellValue();

            case STRING:
                return upperCaseAndTrim(cell.getStringCellValue());

            case DATE, DATETIME:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return dateFormat.format(cell.getDateCellValue());
                } else if (cell.getCellType() == CellType.NUMERIC) {
                    return LocalDate.ofEpochDay((long) cell.getNumericCellValue()).toString();
                } else {
                    return formatter.formatCellValue(cell);
                }

            case DATAFORMAT:
                return upperCaseAndTrim(formatter.formatCellValue(cell));

            case ALFANUMERIC:
                return switch (cell.getCellType()) {
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
                        yield upperCaseAndTrim(formatter.formatCellValue(cell));
                    }
                    default -> null;
                };

            default:
                return cell.getNumericCellValue();
        }
    }

    public static void setCellValue(CellInfo cellInfo, Cell cell, Object value) {
        switch (cellInfo.getCellType()) {
            case BOOLEAN -> cell.setCellValue((boolean) value);
            case STRING -> cell.setCellValue((String) value);
            case DATE, DATETIME -> {
                if (value instanceof Date dateValue) {
                    cell.setCellValue(dateValue);
                } else if (value instanceof String stringValue) {
                    try {
                        Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(stringValue);
                        cell.setCellValue(parsedDate);
                    } catch (ParseException e) {
                        throw new ExcelException("Invalid date format: " + stringValue);
                    }
                }
            }

            case FORMULA -> cell.setCellFormula((String) value);
            default -> {
                if (value instanceof Double d) {
                    cell.setCellValue(BankerRounding.round(d));
                } else if (value instanceof Float f) {
                    cell.setCellValue(BankerRounding.round(f));
                } else if (value instanceof BigDecimal bd) {
                    cell.setCellValue(BankerRounding.round(bd.doubleValue()));
                } else if (value instanceof Integer i) {
                    cell.setCellValue(i);
                } else if (value instanceof Long l) {
                    cell.setCellValue(l);
                }
            }
        }
    }

    public static boolean isEndOfContent(Integer rowCursor, Sheet sheetToRead) {
        return rowCursor > sheetToRead.getLastRowNum();
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null || row.getLastCellNum() <= 0) {
            return true;
        }

        return IntStream.range(row.getFirstCellNum(), row.getLastCellNum())
                .mapToObj(row::getCell)
                .noneMatch(cell -> cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString()));
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    public static void readCell(Cell cell, BeanField beanField, CellInfo cellInfo, Object bean, DataFormat dataFormatter, DataFormatter formatter) {
        try {
            beanField.setFieldValue(ExcelUtils.getValueFromCell(cellInfo, cell, dataFormatter, formatter), bean);
        } catch (Exception e) {
            String cellAddress = (cell != null) ? cell.getAddress().formatAsString() : "Unknown";
            LOGGER.error("Error processing cell {}:", cellAddress, e);
            throw new ExcelException("Invalid Excel content at cell " + cellAddress, e);
        }
    }

    public static boolean isSheetEmpty(Sheet currentSheet) {
        return IntStream.rangeClosed(0, currentSheet.getLastRowNum())
                .mapToObj(currentSheet::getRow)
                .allMatch(ExcelUtils::isRowEmpty);
    }

    public static SheetIndex getSheetIndexInfo(SheetIndexAnnotationProcessor<?> sheetIndexAnnotationProcessor, Object bean) {
        BeanField annotatedField = sheetIndexAnnotationProcessor.getBeanField();
        Assert.notNull(annotatedField, "Must be a field annotated as SheetIndex  ");
        return new SheetIndex((Integer) annotatedField.getFieldValue(bean));
    }

    public static String upperCaseAndTrim(String value) {
        if (value != null) {
            value = value.trim();
            return value.toUpperCase();
        }
        return null;
    }
}
