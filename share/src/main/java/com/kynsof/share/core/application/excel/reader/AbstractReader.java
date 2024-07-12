package com.kynsof.share.core.application.excel.reader;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.application.excel.procesor.ColumPositionAnnotationProcessor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.util.Objects;

@Getter
public abstract class AbstractReader<T> {

    public Workbook workbook;

    public Sheet sheetToRead;

    public final ReaderConfiguration readerConfiguration;

    protected Integer rowCursor;

    protected final Class<T> type;

    protected ColumPositionAnnotationProcessor<T> columPositionAnnotationProcessor;

    public AbstractReader(ReaderConfiguration readerConfiguration, Class<T> type) {
        this.readerConfiguration = readerConfiguration;
        this.type = type;
        init();
    }

    private void init() {
        columPositionAnnotationProcessor = new ColumPositionAnnotationProcessor<T>();
        columPositionAnnotationProcessor.process(type);
        try {
            this.workbook = WorkbookFactory.create(readerConfiguration.getInputStream());
            if (readerConfiguration.isReadLastActiveSheet()) {
                sheetToRead = workbook.getSheetAt(workbook.getActiveSheetIndex());
            } else if (Objects.nonNull(readerConfiguration.getSheetNameToRead()) &&
                    !readerConfiguration.getSheetNameToRead().isEmpty()) {
                sheetToRead = workbook.getSheet(readerConfiguration.getSheetNameToRead());
            }
            if (Objects.isNull(rowCursor)){
                this.rowCursor =readerConfiguration.isIgnoreHeaders()?1:0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public abstract T readSingleLine();

}
