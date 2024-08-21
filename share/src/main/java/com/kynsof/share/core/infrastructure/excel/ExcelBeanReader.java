package com.kynsof.share.core.infrastructure.excel;

import com.kynsof.share.core.application.excel.BeanField;
import com.kynsof.share.core.application.excel.CellInfo;
import com.kynsof.share.core.application.excel.ExcelUtils;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.application.excel.reader.AbstractReader;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.response.ErrorField;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


public class ExcelBeanReader<T> extends AbstractReader<T> {

    public ExcelBeanReader(ReaderConfiguration readerConfiguration, Class<T> type) {
        super(readerConfiguration, type);
    }

    @Override
    public void close() {
        if (Objects.nonNull(workbook)) {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            annotatedField.clear();
        }
    }

    @Override
    public T readSingleLine() {
        T bean;
        if (!ExcelUtils.isEndOfContent(rowCursor, sheetToRead)) {
            try {
                bean = type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (readerConfiguration.isStrictHeaderOrder()){
                this.hasValidHeaderOrder();
            }
            Row currentRow = sheetToRead.getRow(rowCursor);
            DataFormatter formatter = new DataFormatter();
            DataFormat dataFormat =workbook.createDataFormat();
            if (!ExcelUtils.isRowEmpty(currentRow)) {
                annotatedField.forEach((cellInfo, beanField) -> {
                    if (cellInfo.getPosition() != -1) {
                        Cell cell = currentRow.getCell(cellInfo.getPosition(), Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        ExcelUtils.readCell(cell, beanField, cellInfo, bean,dataFormat ,formatter);
                    } else {
                        beanField.setFieldValue(currentRow.getRowNum()+1, bean);
                    }
                });
                rowCursor++;
                return bean;
            }
        }
        rowCursor = null;
        return null;
    }

    @Override
    public void hasContent() {
        boolean empty=true;
        for(int i=readerConfiguration.isIgnoreHeaders()?1:0;i<=sheetToRead.getLastRowNum();i++){
            if(!ExcelUtils.isRowEmpty(sheetToRead.getRow(i))){
                empty= false;
                break;
            }
        }
        if (ExcelUtils.isSheetEmpty(sheetToRead) || empty) {
            throw new ExcelException( "There is no data to import.");
        }
    }

    private void hasValidHeaderOrder(){
        Row header = sheetToRead.getRow(0);
        if(ExcelUtils.isRowEmpty(header)){
            throw new ExcelException("Invalid excel content");
        }
        for (CellInfo cellInfo : annotatedField.keySet()) {
            if (cellInfo.getPosition() != -1) {
                String cellValue = header.getCell(cellInfo.getPosition()).getStringCellValue();
                if (!cellInfo.getHeaderName().equals(cellValue)) {
                    throw new ExcelException("Invalid excel content");
                }
            }
        }
    }

}
