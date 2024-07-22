package com.kynsof.share.core.infrastructure.excel;

import com.kynsof.share.core.application.excel.BeanField;
import com.kynsof.share.core.application.excel.CellInfo;
import com.kynsof.share.core.application.excel.ExcelUtils;
import com.kynsof.share.core.application.excel.SheetIndex;
import com.kynsof.share.core.application.excel.procesor.ColumPositionAnnotationProcessor;
import com.kynsof.share.core.application.excel.procesor.SheetIndexAnnotationProcessor;
import com.kynsof.share.core.application.excel.writer.ExcelWriter;
import com.kynsof.share.core.application.excel.writer.WriterConfiguration;
import com.kynsof.share.core.domain.EWorkbookFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.OutputStream;
import java.util.Map;


public class ExcelBeanWriter<T> extends ExcelWriter<T> {

    public ExcelBeanWriter(WriterConfiguration<T> writerConfiguration) {
        super(writerConfiguration);
    }

    @Override
    public void write(OutputStream outputStream) {
        try {
            WriterConfiguration<T> writerConfiguration = getWriterConfiguration();
            SheetIndexAnnotationProcessor<T> sheetIndexAnnotationProcessor = new SheetIndexAnnotationProcessor<>();
            ColumPositionAnnotationProcessor<T> columPositionAnnotationProcessor = new ColumPositionAnnotationProcessor<>();
            Workbook workbook = WorkbookFactory.create(writerConfiguration.getEWorkbookFormat().equals(EWorkbookFormat.XLSX));
            writerConfiguration.getSheetsNames().forEach(workbook::createSheet);

            for (int i = 0; i < writerConfiguration.getRows().size(); i++) {
                T bean = writerConfiguration.getRows().get(i);
                sheetIndexAnnotationProcessor.process((Class<T>) bean.getClass());
                columPositionAnnotationProcessor.process((Class<T>) bean.getClass());
                SheetIndex sheetIndex = ExcelUtils.getSheetIndexInfo(sheetIndexAnnotationProcessor,bean);
                Sheet sheetToWrite = workbook.getSheetAt(sheetIndex.getSheetIndex());
                Row row = sheetToWrite.createRow(i);
                createRowCells(row, columPositionAnnotationProcessor, bean);
            }
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createRowCells(Row row, ColumPositionAnnotationProcessor<T> columPositionAnnotationProcessor, T bean) {
        Map<CellInfo, BeanField> fields = columPositionAnnotationProcessor.getAnnotatedFields();
        fields.forEach((cellInfo, beanField) -> {
            Cell cell = row.createCell(cellInfo.getPosition());
            ExcelUtils.setCellValue(cellInfo, cell, beanField.getFieldValue(bean), beanField.getFieldType());
        });
    }
}
